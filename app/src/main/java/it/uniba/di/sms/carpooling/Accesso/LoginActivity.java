package it.uniba.di.sms.carpooling.Accesso;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import it.uniba.di.sms.carpooling.HomeActivity;
import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.RequestHandler;
import it.uniba.di.sms.carpooling.SharedPrefManager;
import it.uniba.di.sms.carpooling.URLs;
import it.uniba.di.sms.carpooling.Utente;

public class LoginActivity extends Activity {

    EditText txtUserName;
    TextInputEditText txtPassword;
    RelativeLayout loginForm;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //get the session's token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();
                    }
                });
        txtUserName = findViewById(R.id.usernameLogin);
        txtPassword = findViewById(R.id.passwordLogin);

        loginForm = findViewById(R.id.form_login);

        // Check if user is already logged in
        if(SharedPrefManager.getInstance(LoginActivity.this).isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            loginForm.setVisibility(View.VISIBLE);
        }

        //calling the method login on click
        findViewById(R.id.signInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        //open RegistrationActivity on click
        findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });


    }

    private void userLogin() {
        //first getting the values
        final String username = txtUserName.getText().toString();
        final String password = txtPassword.getText().toString();

        //validating inputs
        if (TextUtils.isEmpty(username)) {
            txtUserName.setError(getText(R.string.enter_username));
            txtUserName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            txtPassword.setError(getText(R.string.enter_password));
            txtPassword.requestFocus();
            return;
        }

        //if everything is fine

        class UserLogin extends AsyncTask<Void, Void, String> {

          private ProgressBar progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar =findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");
                        Utente user;

                        if(userJson.getString("azienda")!=null){
                            //creating a new user object with company
                            user = new Utente(
                                    userJson.getString("username"),
                                    userJson.getString("nome"),
                                    userJson.getString("cognome"),
                                    userJson.getString("indirizzo"),
                                    userJson.getString("email"),
                                    userJson.getString("telefono"),
                                    userJson.getString("dataNascita"),
                                    userJson.getString("azienda"),
                                    userJson.getString("indirizzoAzienda"),
                                    Integer.parseInt(userJson.getString("autorizzato")),
                                    userJson.getString("foto")
                            );
                        }
                        else{
                            //creating a new user object without company
                            user = new Utente(
                                    userJson.getString("username"),
                                    userJson.getString("nome"),
                                    userJson.getString("cognome"),
                                    userJson.getString("indirizzo"),
                                    userJson.getString("email"),
                                    userJson.getString("telefono"),
                                    userJson.getString("dataNascita"),
                                    Integer.parseInt(userJson.getString("autorizzato")),
                                    userJson.getString("foto")
                            );
                        }
                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        finish();

                        //open HomeActivity
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.invalid, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Username", username);
                params.put("Password", password);
                params.put("Token", token);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_LOGIN, params);
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
    }
}

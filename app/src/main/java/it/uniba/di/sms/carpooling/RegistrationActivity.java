package it.uniba.di.sms.carpooling;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {
    EditText txtUserName;
    EditText txtPassword;
    EditText txtIndirizzo;
    EditText txtTelefono;
    EditText txtNome;
    EditText txtCognome;
    EditText txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        txtUserName = findViewById(R.id.usernameText2);
        txtPassword = findViewById(R.id.passwordText2);
        txtIndirizzo = findViewById(R.id.addressText);
        txtTelefono = findViewById(R.id.telText);
        txtNome = findViewById(R.id.nomeText);
        txtCognome = findViewById(R.id.cognomeText);
        txtEmail = findViewById(R.id.emailText);


        Button btnAvanti = findViewById(R.id.avantiButton);

        btnAvanti.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }

        private void registerUser() {
            final String username = txtUserName.getText().toString().trim();
            final String password = txtPassword.getText().toString().trim();
            final String indirizzo = txtIndirizzo.getText().toString().trim();
            final String telefono = txtTelefono.getText().toString().trim();
            final String nome = txtNome.getText().toString().trim();
            final String cognome = txtCognome.getText().toString().trim();
            final String email = txtEmail.getText().toString().trim();
            final String sesso = "m";

            //first we will do the validations

            if (TextUtils.isEmpty(username)) {
                txtUserName.setError("Please enter username");
                txtUserName.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                txtPassword.setError("Please enter your email");
                txtPassword.requestFocus();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                txtEmail.setError("Enter a valid email");
                txtEmail.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                txtPassword.setError("Enter a password");
                txtPassword.requestFocus();
                return;
            }

            //if it passes all the validations

            class RegisterUser extends AsyncTask<Void, Void, String> {

                private ProgressBar progressBar;

                @Override
                protected String doInBackground(Void... voids) {
                    //creating request handler object
                    RequestHandler requestHandler = new RequestHandler();

                    //creating request parameters
                    HashMap<String, String> params = new HashMap<>();
                    params.put("Username", username);
                    params.put("Password", email);
                    params.put("Nome", nome);
                    params.put("Cognome", cognome);
                    params.put("Sesso", sesso);
                    params.put("Indirizzo", indirizzo);
                    params.put("Email", email);
                    params.put("Telefono", telefono);

                    //returing the response
                    return requestHandler.sendPostRequest(URLs.URL_REGISTER, params);
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    //displaying the progress bar while user registers on the server
                   // progressBar = (ProgressBar) findViewById(R.id.progressBar);
                   // progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    //hiding the progressbar after completion
                   // progressBar.setVisibility(View.GONE);

                    try {
                        //converting response to json object
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        JSONObject obj = new JSONObject(s);


                        //if no error in response
                        if (!obj.getBoolean("error")) {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                            //getting the user from the response
                            JSONObject userJson = obj.getJSONObject("user");

                            //creating a new user object
                            Utente user = new Utente(
                                    userJson.getString("username"),
                                    userJson.getString("nome"),
                                    userJson.getString("cognome"),
                                    userJson.getString("sesso"),
                                    userJson.getString("indirizzo"),
                                    userJson.getString("email"),
                                    userJson.getString("telefono"),
                                    userJson.getString("dataNascita")
                            );

                            //storing the user in shared preferences
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                            //starting the profile activity
                            finish();
                            Toast.makeText(getApplicationContext(), "tutto apposto", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            //executing the async task
            RegisterUser ru = new RegisterUser();
            ru.execute();
        }
}

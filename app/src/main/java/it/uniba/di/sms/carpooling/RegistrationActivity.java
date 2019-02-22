package it.uniba.di.sms.carpooling;


import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;


public class RegistrationActivity extends FragmentActivity implements RegistrationFragment.OnFragmentInteractionListener {

    private EditText txtUserName;
    private TextInputEditText txtPassword;
    private EditText txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        txtUserName = findViewById(R.id.usernameRegistration);
        txtPassword = findViewById(R.id.passwordRegistration);
        txtEmail = findViewById(R.id.emailRegistration);

        ImageButton btnAvanti= findViewById(R.id.avantiButton);

        btnAvanti.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                firstRegistrationPart();

            }
        });


    }


    //Apre RegistrationFragment
    public void openFragment(String username, String password, String email) {
        RegistrationFragment fragment = RegistrationFragment.newInstance(username, password, email);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.registration_frag_layout, fragment, "BLANK_FRAGMENT").commitAllowingStateLoss();

    }


    private void firstRegistrationPart() {
        final String username = txtUserName.getText().toString().trim();
        final String password = txtPassword.getText().toString().trim();
        final String email = txtEmail.getText().toString().trim();

        //first we will do the validations
        if (TextUtils.isEmpty(username)) {
            txtUserName.setError("Please enter username");
            txtUserName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
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
        class FirstRegistrationPart extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Username", username);
                params.put("Email",email);

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_CHECKUSER, params);
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

                        //apre il fragment successivo
                        openFragment(username,password,email);

                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        //executing the async task
        FirstRegistrationPart frp = new FirstRegistrationPart();
        frp.execute();
    }





    @Override
    public void onFragmentInteraction(Uri uri) {
        onBackPressed();
    }
}





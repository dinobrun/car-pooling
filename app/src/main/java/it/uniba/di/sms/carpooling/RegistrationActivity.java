package it.uniba.di.sms.carpooling;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity implements RegistrationFragment.OnFragmentInteractionListener {
    EditText txtUserName;
    EditText txtPassword;
    EditText txtEmail;


    private FrameLayout fragmentContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        txtUserName = findViewById(R.id.usernameText2);
        txtPassword = findViewById(R.id.passwordText2);
        txtEmail = findViewById(R.id.emailText);



        //fragment
        fragmentContainer = (FrameLayout) findViewById(R.id.registration_frag_layout);


        Button btnAvanti = findViewById(R.id.avantiButton);

        btnAvanti.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //registerUser();
                firstRegistrationPart();
            }
        });

        List<String> labels = getCompanies();

        //spinner
        Spinner spinner = findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);


    }




    public void openFragment(String username, String password, String email) {

        RegistrationFragment fragment = RegistrationFragment.newInstance(username, password, email);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.registration_frag_layout, fragment, "BLANK_FRAGMENT").commit();
    }







    //////////////////////////


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

                //returing the response
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





    /////////////////////////////








        private ArrayList<String> getCompanies(){

            final ArrayList<String> aziende = new ArrayList<>();

            //classe per prendere le aziende
            class Companies extends AsyncTask<Void, Void, String> {


                @Override
                protected String doInBackground(Void... voids) {

                    //creating request handler object
                    RequestHandler requestHandler = new RequestHandler();
                    return requestHandler.sendGetRequest(URLs.URL_GETCOMPANIES);
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    try {

                        //converting response to json object
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();


                        JSONObject obj = new JSONObject(s);


                        //if no error in response
                        if (!obj.getBoolean("error")) {

                            JSONArray companyJson = obj.getJSONArray("azienda");

                            for(int i=0; i<companyJson.length(); i++){
                                JSONObject temp = companyJson.getJSONObject(i);
                                aziende.add(temp.getString("nome"));
                            }

                            //starting the profile activity
                            Toast.makeText(getApplicationContext(), "tutto apposto", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

            Companies companies = new Companies();
            companies.execute();

            return aziende;
        }

    @Override
    public void onFragmentInteraction(Uri uri) {
        onBackPressed();
    }
}

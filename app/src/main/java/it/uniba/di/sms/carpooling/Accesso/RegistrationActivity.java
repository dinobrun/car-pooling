package it.uniba.di.sms.carpooling.Accesso;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.RequestHandler;
import it.uniba.di.sms.carpooling.URLs;


public class RegistrationActivity extends AppCompatActivity implements RegistrationFragment.OnFragmentInteractionListener {

    private EditText txtUserName;
    private TextInputEditText txtPassword;
    private EditText txtEmail;
    private Toolbar toolbar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.registration_activity_forward_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.forwardItem:
                firstRegistrationPart();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        txtUserName = findViewById(R.id.textUsernameRegistration);
        txtPassword = findViewById(R.id.textPasswordRegistration);
        txtEmail = findViewById(R.id.textEmailRegistration);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openLogin = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(openLogin);
            }
        });
    }


    //Apre RegistrationFragment
    public void openRegistrationFragment(String username, String password, String email) {
        RegistrationFragment fragment = RegistrationFragment.newInstance(username, password, email);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.registration_frag_layout, fragment, "BLANK_FRAGMENT").commitAllowingStateLoss();

    }


    private void firstRegistrationPart() {
        final String username = txtUserName.getText().toString().trim();
        final String password = txtPassword.getText().toString().trim();
        final String email = txtEmail.getText().toString().trim();

        //first we will do the validations
        if (TextUtils.isEmpty(username)) {
            txtUserName.setError(getText(R.string.enter_username));
            txtUserName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            txtPassword.setError(getText(R.string.enter_email));
            txtPassword.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError(getText(R.string.invalid_email));
            txtEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            txtPassword.setError(getText(R.string.enter_password));
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

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        //open RegistrationFragment
                        openRegistrationFragment(username,password,email);
                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
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





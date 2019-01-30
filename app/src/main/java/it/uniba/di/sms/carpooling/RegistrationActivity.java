package it.uniba.di.sms.carpooling;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        final EditText txtUserName = findViewById(R.id.usernameText2);
        final EditText txtPassword = findViewById(R.id.passwordText2);
        Button btnRegister = findViewById(R.id.registerButton2);

        btnRegister.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String username = txtUserName.getText().toString();
                String password = txtPassword.getText().toString();
                try{
                    if(username.length() > 0 && password.length() >0)
                    {
                        DBUser dbUser = new DBUser(RegistrationActivity.this);
                        dbUser.open();

                        dbUser.AddUser(username,password);
                        Toast.makeText(RegistrationActivity.this,"Successfully Registered", Toast.LENGTH_LONG).show();

                        dbUser.close();

                        Intent login=new Intent(RegistrationActivity.this,LoginActivity.class);
                        startActivity(login);
                    }

                }catch(Exception e)
                {
                    Toast.makeText(RegistrationActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        });
    }
}

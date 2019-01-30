package it.uniba.di.sms.carpooling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText txtUserName = findViewById(R.id.usernameText);
        final EditText txtPassword = findViewById(R.id.passwordText);
        Button btnLogin = findViewById(R.id.signInButton);
        Button btnRegister = findViewById(R.id.registerButton);
        btnLogin.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                String username = txtUserName.getText().toString();
                String password = txtPassword.getText().toString();
                try{
                    if(username.length() > 0 && password.length() >0)
                    {
                        DBUser dbUser = new DBUser(LoginActivity.this);
                        dbUser.open();

                        if(dbUser.Login(username, password))
                        {
                            Toast.makeText(LoginActivity.this,"Successfully Logged In", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(LoginActivity.this,"Invalid Username/Password", Toast.LENGTH_LONG).show();
                        }
                        dbUser.close();
                    }

                }catch(Exception e)
                {
                    Toast.makeText(LoginActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        });

        btnRegister.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                try{
                    Intent register=new Intent(LoginActivity.this,RegistrationActivity.class);
                    startActivity(register);

                }catch(Exception e)
                {
                    Toast.makeText(LoginActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        });



    }
}

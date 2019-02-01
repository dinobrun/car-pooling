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
        final EditText txtAddress = findViewById(R.id.addressText);
        final EditText txtTelephone = findViewById(R.id.telText);
        Button btnAvanti = findViewById(R.id.avantiButton);

        btnAvanti.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String username = txtUserName.getText().toString();
                String password = txtPassword.getText().toString();
                String address=txtAddress.getText().toString();
                String telephone= txtTelephone.getText().toString();
                try{
                    if(username.length() > 0 && password.length() >0 && address.length()>0 && telephone.length()>0)
                    {
                        Intent moveTo= new Intent(RegistrationActivity.this, AziendaActivity.class);
                        moveTo.putExtra("Username", username);
                        moveTo.putExtra("Password", password);
                        moveTo.putExtra("Address", address);
                        moveTo.putExtra("Telephone", telephone);
                        startActivity(moveTo);
                    }
                }catch(Exception e)
                {
                    Toast.makeText(RegistrationActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        });
    }
}

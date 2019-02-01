package it.uniba.di.sms.carpooling;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AziendaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_azienda);

        Bundle passedData= getIntent().getExtras();
        final String username=passedData.getString("Username");
        final String password=passedData.getString("Password");
        String address=passedData.getString("Address");
        String telephone=passedData.getString("Telephone");
        
        final EditText txtAzienda = findViewById(R.id.aziendaText);
        Button btnRegister = findViewById(R.id.registerButton2);

        btnRegister.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String azienda = txtAzienda.getText().toString();
                try{
                    if(azienda.length() > 0)
                    {
                        DBUser dbUser = new DBUser(AziendaActivity.this);
                        dbUser.open();

                        dbUser.AddUser(username,password);
                        Toast.makeText(AziendaActivity.this,"Successfully Registered", Toast.LENGTH_LONG).show();

                        dbUser.close();

                        Intent login=new Intent(AziendaActivity.this,LoginActivity.class);
                        startActivity(login);
                    }

                }catch(Exception e)
                {
                    Toast.makeText(AziendaActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        });


    }
}

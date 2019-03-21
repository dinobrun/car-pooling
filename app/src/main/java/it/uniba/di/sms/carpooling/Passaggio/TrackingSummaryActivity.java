package it.uniba.di.sms.carpooling.Passaggio;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import it.uniba.di.sms.carpooling.HomeActivity;
import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.RequestHandler;
import it.uniba.di.sms.carpooling.SharedPrefManager;
import it.uniba.di.sms.carpooling.URLs;
import it.uniba.di.sms.carpooling.Utente;

public class TrackingSummaryActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 100;

    private RecyclerView passengersRecycler;
    private PassengersAdapter adapterPassengers;
    String jsonListPassengers;
    ArrayList<Utente> listPassengers = new ArrayList<>();
    Utente driver;
    TextView txtDriver;
    TextView txtScore;

    boolean isCorrectEnd;

    Button finishButton;
    int idPassaggio;

    int score;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isCorrectEnd = getIntent().getExtras().getBoolean("correct_end_key");
        idPassaggio = getIntent().getExtras().getInt("id_passaggio");

        //view of correct completed tracking
        if(isCorrectEnd){
            setContentView(R.layout.activity_tracking_summary);

            txtDriver = findViewById(R.id.txtDriver);
            txtScore = findViewById(R.id.score);

            finishButton = findViewById(R.id.finish_button);
            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent goToHomeActivity = new Intent(TrackingSummaryActivity.this, HomeActivity.class);
                    startActivity(goToHomeActivity);
                }
            });

            //set adapter for paassengers list
            passengersRecycler = findViewById(R.id.passengersRecycler);
            passengersRecycler.setHasFixedSize(true);
            passengersRecycler.setLayoutManager(new LinearLayoutManager(this));

            adapterPassengers = new PassengersAdapter(TrackingSummaryActivity.this, listPassengers);

            //setting adapter to recyclerview
            passengersRecycler.setAdapter(adapterPassengers);


            ArrayList<Utente> tempListPassengers = new ArrayList<>();
            // Extract data included in the Intent
            jsonListPassengers = getIntent().getStringExtra("data_tracking");

            try {

                JSONObject obj = new JSONObject(jsonListPassengers);
                JSONObject driverJson = obj.getJSONObject("driver");
                driver = new Utente(driverJson.getString("nome"), driverJson.getString("cognome"));
                txtDriver.setText(driverJson.getString("nome")+" "+driverJson.getString("cognome"));


                JSONArray passeggeriJson = obj.getJSONArray("passengers");
                for(int i=0; i<passeggeriJson.length(); i++){
                    JSONObject temp = passeggeriJson.getJSONObject(i);
                    tempListPassengers.add(new Utente(
                            temp.getString("nome"),
                            temp.getString("cognome")
                    ));
                }
                //check if there is a new user to insert
                for(Utente user: tempListPassengers){
                    if(!listPassengers.contains(user)){
                        listPassengers.add(user);
                        adapterPassengers.notifyDataSetChanged();
                    }
                }

                tempListPassengers.clear();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //view of stopped tracking before complete
        else{
            setContentView(R.layout.activity_stopped_tracking_summary);

            finishButton = findViewById(R.id.finish_button);

            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent goToHomeActivity = new Intent(TrackingSummaryActivity.this, HomeActivity.class);
                    startActivity(goToHomeActivity);
                }
            });
        }
    }


    private void getScore() {

        //if it passes all the validations
        class Score extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("ID_Passaggio",Integer.toString(idPassaggio));
                params.put("Username", SharedPrefManager.getInstance(TrackingSummaryActivity.this).getUser().getUsername());

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_START_TRACKING, params);
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

                        score = Integer.parseInt(obj.getString("score"));

                        txtScore.setText(Integer.toString(score));


                    } else {
                        Toast.makeText(TrackingSummaryActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        //executing the async task
        Score s = new Score();
        s.execute();
    }





}


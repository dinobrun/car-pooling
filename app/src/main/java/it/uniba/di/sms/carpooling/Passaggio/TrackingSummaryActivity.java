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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_summary);

        txtDriver = findViewById(R.id.txtDriver);
        txtScore = findViewById(R.id.score);

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

        //int idPassaggio = getIntent().getExtras().getInt("id_passaggio");

    }





}


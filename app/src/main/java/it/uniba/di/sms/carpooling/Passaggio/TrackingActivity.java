package it.uniba.di.sms.carpooling.Passaggio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.content.Intent;
import android.location.LocationManager;
import android.Manifest;
import android.content.pm.PackageManager;
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

import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.Utente;

public class TrackingActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 100;

    private RecyclerView passengersRecycler;
    private PassengersAdapter adapterPassengers;
    String jsonListPassengers;
    ArrayList<Utente> listPassengers = new ArrayList<>();
    Utente driver;
    TextView txtDriver;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Premi di nuovo BACK per chiudere il tracking", Toast.LENGTH_SHORT).show();
        stopTrackerService();


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


    // Handling the received Intents for the "my-integer" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        ArrayList<Utente> tempListPassengers = new ArrayList<>();
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            jsonListPassengers = intent.getStringExtra("listPassengers");

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
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        Toolbar toolbar = findViewById(R.id.my_toolbar);

        txtDriver = findViewById(R.id.txtDriver);

        //set adapter for paassengers list
        passengersRecycler = findViewById(R.id.passengersRecycler);
        passengersRecycler.setHasFixedSize(true);
        passengersRecycler.setLayoutManager(new LinearLayoutManager(this));

        adapterPassengers = new PassengersAdapter(TrackingActivity.this, listPassengers);

        //setting adapter to recyclerview
        passengersRecycler.setAdapter(adapterPassengers);



        (TrackingActivity.this).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.close_icon);
        toolbar.setTitle("Tracciamento del percorso");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        int idPassaggio = getIntent().getExtras().getInt("id_passaggio");

       // adapterPassengers = new PassaggioOffertoAdapter(this, listaPassaggi);


//Check whether GPS tracking is enabled//

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
        }

//Check whether this app has access to the location permission//

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

//If the location permission has been granted, then start the TrackerService//

        if (permission == PackageManager.PERMISSION_GRANTED) {
            //stopTrackerService();
        }
    }


    private void stopTrackerService() {
        Intent serviceIntent = new Intent(this, TrackingService.class);
        stopService(serviceIntent);
        onBackPressed();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // This registers mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver,
                        new IntentFilter("my-integer"));
    }
}


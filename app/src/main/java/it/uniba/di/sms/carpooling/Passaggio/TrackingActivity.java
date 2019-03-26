package it.uniba.di.sms.carpooling.Passaggio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
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
import java.util.HashMap;

import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.RequestHandler;
import it.uniba.di.sms.carpooling.SharedPrefManager;
import it.uniba.di.sms.carpooling.URLs;
import it.uniba.di.sms.carpooling.Utente;

public class TrackingActivity extends AppCompatActivity {


    private RecyclerView passengersRecycler;
    private PassengersAdapter adapterPassengers;
    String jsonListPassengers;
    ArrayList<Utente> listPassengers = new ArrayList<>();
    Utente driver;
    TextView txtDriver;
    int idPassaggio;

    boolean doubleBackToExitPressedOnce = false;
    boolean isPassenger;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;

        Toast.makeText(this, getString(R.string.back_again), Toast.LENGTH_SHORT).show();
        cancelTracking();


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


        toolbar.setTitle(R.string.ride_tracking);
        (TrackingActivity.this).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.close_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        idPassaggio = getIntent().getExtras().getInt("id_passaggio");
        isPassenger = getIntent().getExtras().getBoolean("passenger");

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
        Intent goToSummary = new Intent(this, TrackingSummaryActivity.class);
        goToSummary.putExtra("correct_end_key",false);
        goToSummary.putExtra("id_passaggio",idPassaggio);
        startActivity(goToSummary);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // This registers mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver,
                        new IntentFilter("my-integer"));
    }




    private void cancelTracking() {

        //if it passes all the validations
        class CancelTracking extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("ID_Passaggio",Integer.toString(idPassaggio));
                params.put("Username", SharedPrefManager.getInstance(TrackingActivity.this).getUser().getUsername());
                params.put("Passenger",Boolean.toString(isPassenger));

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_CANCEL_TRACKING, params);
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

                    //Toast.makeText(TrackingService.this, s, Toast.LENGTH_SHORT).show();

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        stopTrackerService();
                        Toast.makeText(TrackingActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TrackingActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        //executing the async task
        CancelTracking cl = new CancelTracking();
        cl.execute();
    }
}


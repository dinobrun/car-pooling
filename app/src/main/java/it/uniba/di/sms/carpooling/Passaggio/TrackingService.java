package it.uniba.di.sms.carpooling.Passaggio;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.os.IBinder;
import android.content.Intent;
import android.content.IntentFilter;
import android.Manifest;
import android.location.Location;
import android.content.pm.PackageManager;
import android.app.PendingIntent;
import android.app.Service;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

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


public class TrackingService extends Service {


    LocationRequest request = new LocationRequest();
    FusedLocationProviderClient client;
    FusedLocationProviderClient clienTracking;
    LocationCallback locationCallback;
    LocationCallback locationCallbackTracking;
    int idPassaggio;

    boolean passenger;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        client.removeLocationUpdates(locationCallbackTracking);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        idPassaggio = intent.getExtras().getInt("id_passaggio");

        passenger = intent.getExtras().getBoolean("passenger");

        requestLocationCheckPassaggio();

        return START_STICKY;
    }


    private void createNotification() {
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
        }

            Intent goToTracking = new Intent(TrackingService.this, TrackingActivity.class);
            goToTracking.putExtra("id_passaggio", idPassaggio);

            String stop = "stop";
            registerReceiver(stopReceiver, new IntentFilter(stop));
            PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                    this, 0, goToTracking, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_icon_start)
                    .setContentTitle(getString(R.string.tracking_in_progress))
                    .setContentText(getString(R.string.close_tracking))
                    .setContentIntent(broadcastIntent)
                    .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                    .setCategory(Notification.CATEGORY_TRANSPORT)
                    .build();
            startForeground(2, notification);
    }




    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
        }
    };


    //invia la posizione del dispositivo al server per la validazione del passaggio
    private void sendLocationForValidation(final Location location) {

        //if it passes all the validations
        class LocationUser extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Username", SharedPrefManager.getInstance(TrackingService.this).getUser().getUsername());
                params.put("ID_Passaggio", Integer.toString(idPassaggio));
                params.put("Latitudine", Double.toString(location.getLatitude()));
                params.put("Longitudine", Double.toString(location.getLongitude()));

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_CHECK_PASSAGGIO, params);
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

                        //start tracking
                        createNotification();
                        requestLocationTracking();

                        Intent goToTracking = new Intent(TrackingService.this, TrackingActivity.class);
                        goToTracking.putExtra("id_passaggio",idPassaggio);
                        goToTracking.putExtra("passenger",passenger);
                        startActivity(goToTracking);
                    } else {
                        Toast.makeText(TrackingService.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        //executing the async task
        LocationUser lu = new LocationUser();
        lu.execute();
    }

    //first check if location is available
    private void requestLocationCheckPassaggio() {

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        client = LocationServices.getFusedLocationProviderClient(this);

        int permission = ContextCompat.checkSelfPermission(TrackingService.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {

             locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        //invia al server la posizione per la validazione del percorso
                        sendLocationForValidation(location);
                    }
                }
            };
            client.requestLocationUpdates(request, locationCallback, null);
        }
    }

    //start tracking
    private void requestLocationTracking() {
        LocationRequest requestTracking = new LocationRequest();
        requestTracking.setInterval(5000);
        requestTracking.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        clienTracking = LocationServices.getFusedLocationProviderClient(this);


        if (ContextCompat.checkSelfPermission(TrackingService.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationCallbackTracking = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    Location location = locationResult.getLastLocation();

                    if (location != null) {
                        startTrackingService(location);
                    }
                }
            };

            clienTracking.requestLocationUpdates(requestTracking,locationCallbackTracking , null);
        }

    }

    private void startTrackingService(final Location location) {

        //if it passes all the validations
        class LocationUserTracking extends AsyncTask<Void, Void, String> {

            ArrayList<Utente> listPassengers = new ArrayList<>();

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("ID_Passaggio",Integer.toString(idPassaggio));
                params.put("Username", SharedPrefManager.getInstance(TrackingService.this).getUser().getUsername());
                params.put("Latitudine", Double.toString(location.getLatitude()));
                params.put("Longitudine", Double.toString(location.getLongitude()));
                params.put("Passenger",Boolean.toString(passenger));

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

                        //check if user is a passenger
                        if(passenger){
                            //if list is not empty
                            if(!obj.getBoolean("empty_list")){
                                //check if driver is near the company
                                if(obj.getBoolean("ended")){
                                    sendListUser(s);
                                    stopSelf();
                                    Intent goToTrackingSummary = new Intent(TrackingService.this, TrackingSummaryActivity.class);
                                    goToTrackingSummary.putExtra("data_tracking",s);
                                    goToTrackingSummary.putExtra("correct_end_key",true);
                                    goToTrackingSummary.putExtra("id_passaggio",idPassaggio);
                                    startActivity(goToTrackingSummary);
                                }
                                else if(obj.getBoolean("sudden_end")){
                                    stopSelf();
                                    Intent goToTrackingSummary = new Intent(TrackingService.this, TrackingSummaryActivity.class);
                                    goToTrackingSummary.putExtra("data_tracking",s);
                                    goToTrackingSummary.putExtra("correct_end_key",false);
                                    goToTrackingSummary.putExtra("id_passaggio",idPassaggio);
                                    startActivity(goToTrackingSummary);
                                }
                                sendListUser(s);
                            }
                        }
                        //check if user is a driver
                        else{
                            //if list is not empty
                            if(!obj.getBoolean("empty_list")){
                                //check if driver is near the company
                                if(obj.getBoolean("nearby")){
                                    sendListUser(s);

                                    //correct stop for the ride
                                    stopSelf();

                                    Intent goToTrackingSummary = new Intent(TrackingService.this, TrackingSummaryActivity.class);
                                    goToTrackingSummary.putExtra("data_tracking",s);
                                    goToTrackingSummary.putExtra("correct_end_key",true);
                                    goToTrackingSummary.putExtra("id_passaggio",idPassaggio);
                                    startActivity(goToTrackingSummary);
                                }


                                sendListUser(s);
                            }
                        }

                    } else {
                        Toast.makeText(TrackingService.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        //executing the async task
        LocationUserTracking lut = new LocationUserTracking();
        lut.execute();
    }

    // send list of passengers to the activity
    private void sendListUser(String json) {
        // The string "my-integer" will be used to filer the intent
        Intent intent = new Intent("my-integer");
        // Adding some data
        intent.putExtra("listPassengers", json);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }




}
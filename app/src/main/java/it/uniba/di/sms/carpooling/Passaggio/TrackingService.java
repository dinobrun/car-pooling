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
import android.support.v4.content.ContextCompat;
import android.os.IBinder;
import android.content.Intent;
import android.content.IntentFilter;
import android.Manifest;
import android.location.Location;
import android.content.pm.PackageManager;
import android.app.PendingIntent;
import android.app.Service;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import it.uniba.di.sms.carpooling.Automobile.AutoAdapter;
import it.uniba.di.sms.carpooling.MapsActivity;
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




    public RecyclerView recyclerViewTracking;
    public PasseggeroAdapter adapterTracking;


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
        Toast.makeText(TrackingService.this, "Servizio chiuso",Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        idPassaggio = intent.getExtras().getInt("id_passaggio");

        requestLocationCheckPassaggio();


        //loginToFirebase();
        return START_STICKY;
    }



    private void createNotification() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);



            String stop = "stop";
            registerReceiver(stopReceiver, new IntentFilter(stop));
            PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                    this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);



            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.close_icon)
                    .setContentTitle("Tracciamento in corso.")
                    .setContentText("Tocca qui per chiudere il tracciamento.")
                    .setContentIntent(broadcastIntent)
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_TRANSPORT)
                    .build();
            startForeground(2, notification);


        }
    }




    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//Unregister the BroadcastReceiver when the notification is tapped//

            //unregisterReceiver(stopReceiver);

//Stop the Service//
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
                        Toast.makeText(TrackingService.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        createNotification();
                        requestLocationTracking();

                        Intent goToTracking = new Intent(TrackingService.this, TrackingActivity.class);
                        goToTracking.putExtra("id_passaggio",idPassaggio);
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


    private void requestLocationCheckPassaggio() {
        request.setInterval(5000);
        request.setNumUpdates(1);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        client = LocationServices.getFusedLocationProviderClient(this);

        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

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


    private void requestLocationTracking() {
        LocationRequest requestTracking = new LocationRequest();
        requestTracking.setInterval(5000);
        requestTracking.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        clienTracking = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {

            locationCallbackTracking = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    //Get a reference to the database, so your app can perform read and write operations//

                    //DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                    Location location = locationResult.getLastLocation();

                    if (location != null) {
                        sendLocationForTracking(location);
                    }
                }
            };

            clienTracking.requestLocationUpdates(requestTracking,locationCallbackTracking , null);
        }
    }


    //invia la posizione del dispositivo al server
    private void sendLocationForTracking(final Location location) {

        //if it passes all the validations
        class LocationUserTracking extends AsyncTask<Void, Void, String> {

            ArrayList<Utente> utentiPasseggeri = new ArrayList<>();

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Username", SharedPrefManager.getInstance(TrackingService.this).getUser().getUsername());
                params.put("Id_Passaggio",Integer.toString(idPassaggio));
                params.put("Latitudine", Double.toString(location.getLatitude()));
                params.put("Longitudine", Double.toString(location.getLongitude()));

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_TRACKING_PASSAGGIO, params);
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
                        Toast.makeText(TrackingService.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        /*
                        //if list is not empty
                        if(!obj.getBoolean("empty_list")){
                            JSONArray passeggeriJson = obj.getJSONArray("passeggeri");
                            for(int i=0; i<passeggeriJson.length(); i++){
                                JSONObject temp = passeggeriJson.getJSONObject(i);
                                utentiPasseggeri.add(new Utente(
                                        temp.getString("username"),
                                        temp.getString("indirizzo")
                                ));
                            }
                        }*/

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


}
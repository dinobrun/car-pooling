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
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Process;
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
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Timer;

import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.RequestHandler;
import it.uniba.di.sms.carpooling.SharedPrefManager;
import it.uniba.di.sms.carpooling.URLs;


public class TrackingService extends Service {


    LocationRequest request = new LocationRequest();
    FusedLocationProviderClient client;
    LocationCallback locationCallback;


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
        client.removeLocationUpdates(locationCallback);
        Toast.makeText(TrackingService.this, "Servizio chiuso",Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        requestLocationCheckPassaggio();
        createNotification();

        Toast.makeText(TrackingService.this, "Servizio partito",Toast.LENGTH_SHORT).show();

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
                    .setSmallIcon(R.drawable.ritorno_icon)
                    .setContentTitle("Carpooling")
                    .setContentText("Tracciamento in corso. Tocca qui per chiudere il tracciamento.")
                    .setContentIntent(broadcastIntent)
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
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


    //restituisce la lista di utenti che hanno richiesto il passaggio
    private void sendLocation(final Location location) {

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

                    } else {
                        //Toast.makeText(TrackingService.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
        //request.setNumUpdates(1);
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
                        sendLocation(location);
                    }
                }
            };




            client.requestLocationUpdates(request, locationCallback, null);




            /*final LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        sendLocation(location);
                        Toast.makeText(TrackingService.this, "giusto", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(TrackingService.this, "non e entrato nell if", Toast.LENGTH_SHORT).show();
                    }
                }
            };*/




        }


    }






//Initiate the request to track the device's location//

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();

//Specify how often your app should request the device’s location//





//Get the most accurate location data available//

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = getString(R.string.firebase_path);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

//If the app currently has access to the location permission...//

        if (permission == PackageManager.PERMISSION_GRANTED) {

//...then request location updates//

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    //Get a reference to the database, so your app can perform read and write operations//

                    //DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                    Location location = locationResult.getLastLocation();


                    if (location != null) {

                        //Save the location data to the database//

                        //ref.setValue(location);
                    }
                }
            }, null);
        }
    }


}
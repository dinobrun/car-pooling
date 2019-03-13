package it.uniba.di.sms.carpooling;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import it.uniba.di.sms.carpooling.Passaggio.Passaggio;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback  {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    private ArrayList<Passaggio> passaggi;
    private ArrayList<String> passaggi_utente;
    CardView card;
    ImageView close;
    TextView txtNome;
    TextView txtTelefono;
    TextView txtAuto;
    TextView txtPosti;
    TextView txtData;
    Button btnRequest;

    private boolean clicked=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        txtNome = findViewById(R.id.txtNome);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtAuto =  findViewById(R.id.txtAuto);
        txtPosti = findViewById(R.id.txtPosti);
        txtData = findViewById(R.id.txtData);
        btnRequest =  findViewById(R.id.btnRequest);
        card = findViewById(R.id.info);
        close = findViewById(R.id.close_card);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsActivity.this.onBackPressed();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.setVisibility(View.INVISIBLE);
            }
        });
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        //lista di passaggi completa
        passaggi = (ArrayList<Passaggio>) getIntent().getSerializableExtra("Passaggi");
        //Toast.makeText(MapsActivity.this, passaggi.get(0).getAutista(),Toast.LENGTH_SHORT).show();

        //lista di ID di passaggi già richiesti
        passaggi_utente = (ArrayList<String>) getIntent().getSerializableExtra("Passaggi_utente");

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //getPassaggi();
        try {
            for(Passaggio p : passaggi)
                if(passaggi_utente.contains(Integer.toString(p.getId()))){
                    p.setRichiesto(true);
                    setMarker(mGoogleMap, p);
                }else{
                    setMarker(mGoogleMap, p);
                }


        } catch (IOException e) {
            e.printStackTrace();
        }

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getTag() != null){
                    displayInfo(marker);
                    return true;
                }else{
                    return false;
                }
            }
        });




        //PERMISSIONS
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }


    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult.getLastLocation() != null) {
                Location location = locationResult.getLastLocation();
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                MarkerOptions markerOptions = new MarkerOptions();

                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Marker della mia posizione
                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                Address address = null;
                try {
                    address = geocoder.getFromLocationName(SharedPrefManager.getInstance(MapsActivity.this).getUser().getIndirizzo(), 1).get(0);
                    markerOptions.position(new LatLng(address.getLatitude(),address.getLongitude()));
                    markerOptions.title("Casa");
                    markerOptions.icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.marker_home));
                    mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    };




    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //crea un riquadro in cui inserisce le info del marker
    public void displayInfo(final Marker marker) {
        //visualizza la card con le informazioni sul passaggio
        card.setVisibility(View.VISIBLE);

        final Passaggio passaggio = (Passaggio) marker.getTag();
        txtNome.setText(passaggio.getNomeAutista() + " " + passaggio.getCognomeAutista());
        txtTelefono.setText(passaggio.getTelefonoAutista());
        txtAuto.setText(passaggio.getAutomobile());
        txtPosti.setText(Integer.toString(passaggio.getNumPosti()));
        txtData.setText(passaggio.getData());

        if(passaggio.isRichiesto()){
            btnRequest.setClickable(false);
            btnRequest.setText("Richiesto");
            btnRequest.setBackgroundColor(R.color.cardview_light_background);
        }else{
            btnRequest.setClickable(true);
            btnRequest.setText("Richiedi passaggio");
            btnRequest.setBackgroundColor(R.color.easyColor);
            btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPassaggio(marker);
                }
            });
        }
    }

    //funzione che come parametri ha una mappa e un utente e gli assegna un marker con le informazioni
    private void setMarker(GoogleMap map, Passaggio passaggio) throws IOException {
        MarkerOptions markerOptions = new MarkerOptions();
        Marker marker;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Address address = geocoder.getFromLocationName(passaggio.getIndirizzo(), 1).get(0);
        markerOptions.position(new LatLng(address.getLatitude(),address.getLongitude()));
        if(passaggio.isRichiesto()){
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            btnRequest.setText("Richiesto");
            btnRequest.setClickable(false);
            btnRequest.setBackgroundColor(R.color.cardview_light_background);
        }
        markerOptions.title(passaggio.getUsernameAutista());
        marker = map.addMarker(markerOptions);
        marker.setTag(passaggio);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), (float) 14));
    }


    //funzione che prenota il passaggio selezionato
    private void requestPassaggio(final Marker markerPassaggio){
            final String username = SharedPrefManager.getInstance(MapsActivity.this).getUser().getUsername();
            final Passaggio passaggio = (Passaggio) markerPassaggio.getTag();

            //if it passes all the validations
            class RequestPassaggio extends AsyncTask<Void, Void, String> {

                @Override
                protected String doInBackground(Void... voids) {
                    //creating request handler object
                    RequestHandler requestHandler = new RequestHandler();

                    //creating request parameters
                    HashMap<String, String> params = new HashMap<>();
                    params.put("Username", username);
                    params.put("ID", Integer.toString(passaggio.getId()));

                    //returning the response
                    return requestHandler.sendPostRequest(URLs.URL_REQUESTPASSAGGIO, params);
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    //displaying the progress bar while user registers on the server
                    // progressBar = (ProgressBar) findViewById(R.id.progressBar);
                    // progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    //hiding the progressbar after completion
                    // progressBar.setVisibility(View.GONE);

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(s);

                        //if no error in response
                        if (!obj.getBoolean("error")) {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            markerPassaggio.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            btnRequest.setText("Richiesto");
                            btnRequest.setClickable(false);
                            btnRequest.setBackgroundColor(R.color.cardview_dark_background);
                        } else {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            //executing the async task
            RequestPassaggio rp = new RequestPassaggio();
            rp.execute();
        }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
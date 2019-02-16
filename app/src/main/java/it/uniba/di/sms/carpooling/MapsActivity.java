package it.uniba.di.sms.carpooling;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback  {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Marker prova1;
    Marker prova2;
    Utente utente1;
    Utente utente2;
    FusedLocationProviderClient mFusedLocationClient;
    ArrayList<Marker> arrayPassaggi = new ArrayList<>();
    private RelativeLayout parentLinearLayout;


    private String data, azienda, direzione;

    Marker casaDino;


    //GEOCODING from ADDRESS
    public LatLng coordsFromAddress() throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocationName("Via Bari Altamura", 1);
        Address address = addresses.get(0);
        LatLng coords = new LatLng(address.getLatitude(),address.getLongitude());
        return coords;
    }

    //GEOCODING from COORDS
    public Address addressFromCoords() throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(coordsFromAddress().latitude,coordsFromAddress().longitude,1);
        return addresses.get(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        parentLinearLayout = findViewById(R.id.mParentProva);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);


        Bundle datipassati = getIntent().getExtras();
        data = datipassati.getString("Data");
        azienda = datipassati.getString("Azienda");
        direzione = datipassati.getString("Direzione");





    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        getPassaggi();

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                displayInfo(parentLinearLayout, marker);
                return true;
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

                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Marker della mia posizione
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrLocationMarker.getPosition(), (float) 14));

                //Marker di un amico attraverso Geocoder
                MarkerOptions amico = new MarkerOptions();
                LatLng positionAmico = new LatLng(1.2,3.4);

                try {
                    positionAmico = coordsFromAddress();
                    amico.position(positionAmico);
                    amico.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    casaDino = mGoogleMap.addMarker(amico);
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
    public void displayInfo(View v, Marker marker) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.info_window, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        TextView txtNome = (TextView) rowView.findViewById(R.id.txtNome);
        TextView txtCognome = (TextView) rowView.findViewById(R.id.txtCognome);

        Utente user = (Utente) marker.getTag();
        txtNome.setText(user.getNome());
        txtCognome.setText(user.getCognome());
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, params);
    }


    //funzione che come parametri ha una mappa e un utente e gli assegna un marker con le informazioni
    private void setMarker(GoogleMap map, Passaggio passaggio) throws IOException {
        MarkerOptions markerOptions = new MarkerOptions();
        Marker marker;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Address address = geocoder.getFromLocationName(passaggio.getIndirizzo(), 1).get(0);
        markerOptions.position(new LatLng(address.getLatitude(),address.getLongitude()));
        markerOptions.title(passaggio.getAutista());
        marker = map.addMarker(markerOptions);
        //marker.setTag(passaggio);
    }





    //METODO CHE RESTITUISCE I PASSAGGI
    private void getPassaggi() {

        //if it passes all the validations
        class GetPassaggio extends AsyncTask<Void, Void, String> {

            //private ProgressBar progressBar;
            final ArrayList<Passaggio> listaPassaggi = new ArrayList<>();

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Data", data);
                params.put("Azienda", azienda);
                params.put("Direzione", direzione);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_GETPASSAGGI, params);
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

                //converting response to json object
                Toast.makeText(MapsActivity.this, s, Toast.LENGTH_SHORT).show();

                try{

                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        //if list is not empty
                        if(!obj.getBoolean("empty_list")){
                            JSONArray passaggiJson = obj.getJSONArray("passaggio");
                            for(int i=0; i<passaggiJson.length(); i++){
                                JSONObject temp = passaggiJson.getJSONObject(i);
                                listaPassaggi.add(new Passaggio(
                                        Integer.parseInt(temp.getString("id")),
                                        temp.getString("nome"),
                                        temp.getString("indirizzo"),
                                        temp.getString("data"),
                                        temp.getString("automobile"),
                                        azienda,
                                        temp.getString("direzione"),
                                        Integer.parseInt(temp.getString("num_posti"))
                                ));
                                //Toast.makeText(getActivity(), Integer.parseInt(temp.getString("id")), Toast.LENGTH_SHORT).show();
                            }

                            setMarker(mGoogleMap,listaPassaggi.get(0));

                        }
                        else{
                            Toast.makeText(MapsActivity.this, "Lista vuota scemo", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(MapsActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }





                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }
        }

        GetPassaggio pg = new GetPassaggio();
        pg.execute();
    }




}
package it.uniba.di.sms.carpooling;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import it.uniba.di.sms.carpooling.Passaggio.Passaggio;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback  {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;
    private ArrayList<Passaggio> rides;
    private ArrayList<String> user_rides;
    CardView card;
    ImageView close;
    TextView txtName;
    TextView txtTelephone;
    TextView txtCar;
    TextView txtSeats;
    TextView txtDate;
    Button btnRequest;
    ImageView callIcon;
    ImageView imageProfile;
    RelativeLayout overlayLayout;
    ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        txtName = findViewById(R.id.txtNome);
        txtTelephone = findViewById(R.id.txtTelefono);
        txtCar =  findViewById(R.id.txtAuto);
        txtSeats = findViewById(R.id.txtPosti);
        txtDate = findViewById(R.id.txtData);
        btnRequest =  findViewById(R.id.btnRequest);
        card = findViewById(R.id.info);
        close = findViewById(R.id.close_card);
        imageProfile=findViewById(R.id.imageProfile);
        overlayLayout = findViewById(R.id.overlayLayout);
        progressBar = findViewById(R.id.progressBar);


        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setTitle(R.string.view_rides);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsActivity.this.onBackPressed();
            }
        });

        callIcon = findViewById(R.id.call_icon);
        callIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + txtTelephone.getText().toString()));
                startActivity(callIntent);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.setVisibility(View.INVISIBLE);
            }
        });
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(MapsActivity.this);
            }
        }, 400);

        //lista di passaggi completa
        rides = (ArrayList<Passaggio>) getIntent().getSerializableExtra("Passaggi");
        //Toast.makeText(MapsActivity.this, passaggi.get(0).getAutista(),Toast.LENGTH_SHORT).show();

        //lista di ID di passaggi già richiesti
        user_rides = (ArrayList<String>) getIntent().getSerializableExtra("Passaggi_utente");

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
            //crea marker sull'azienda
            setCompanyMarker();
            //crea marker sulla casa dell'utente richiedente
            setUserMarker();
            for(Passaggio p : rides)
                if(user_rides.contains(Integer.toString(p.getId()))){
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

    }



    //crea un riquadro in cui inserisce le info del marker
    @SuppressLint("ResourceAsColor")
    public void displayInfo(final Marker marker) {
        //visualizza la card con le informazioni sul passaggio
        card.setVisibility(View.VISIBLE);

        final Passaggio passaggio = (Passaggio) marker.getTag();
        txtName.setText(": " + passaggio.getNomeAutista() + " " + passaggio.getCognomeAutista());
        txtTelephone.setText(": " + passaggio.getTelefonoAutista());
        txtCar.setText(": " + passaggio.getAutomobile());
        txtSeats.setText(": " + Integer.toString(passaggio.getNumPosti()));
        //set date with correct format
        // First convert the String to a Date
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ITALIAN);
        Date date = null;
        try {
            date = dateParser.parse(passaggio.getData());
            // Then convert the Date to a String, formatted as you dd/MM/yyyy
            SimpleDateFormat dateFormatter = new SimpleDateFormat("E d MMM yyyy HH:mm", Locale.ITALY);
            txtDate.setText(": " + dateFormatter.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if(passaggio.isRichiesto()){
            btnRequest.setEnabled(false);
            btnRequest.setText(R.string.requested_rides);
        }else{
            btnRequest.setClickable(true);
            btnRequest.setText(R.string.ask_ride);
           // btnRequest.setBackgroundColor(R.color.cardview_dark_background);
            btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPassaggio(marker);
                }
            });
        }
    }

    //funzione che come parametri ha una mappa e un utente e gli assegna un marker con le informazioni
    @SuppressLint("ResourceAsColor")
    private void setMarker(GoogleMap map, Passaggio passaggio) throws IOException {
        MarkerOptions markerOptions = new MarkerOptions();
        Marker marker;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Address address = geocoder.getFromLocationName(passaggio.getIndirizzo(), 1).get(0);
        markerOptions.position(new LatLng(address.getLatitude(),address.getLongitude()));
        if(passaggio.isRichiesto()){
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            btnRequest.setText(R.string.requested_rides);
            btnRequest.setClickable(false);
            //btnRequest.setBackgroundColor(R.color.cardview_light_background);
        }
        markerOptions.title(passaggio.getUsernameAutista());
        marker = map.addMarker(markerOptions);
        marker.setTag(passaggio);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), (float) 14));
    }

    //funzione che crea il marker sul luogo di lavoro
    private void setCompanyMarker() throws IOException {
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        MarkerOptions markerOptions = new MarkerOptions();
        Address address;
        address = geocoder.getFromLocationName(SharedPrefManager.getInstance(MapsActivity.this).getUser().getIndirizzoAzienda(), 1).get(0);
        markerOptions.position(new LatLng(address.getLatitude(),address.getLongitude()));
        markerOptions.title(SharedPrefManager.getInstance(getApplicationContext()).getUser().getAzienda());
        markerOptions.icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.marker_factory));
        mGoogleMap.addMarker(markerOptions);
    }

    //funzione che crea il marker sull'indirizzo dell'utente che richiede il passaggio
    private void setUserMarker() throws IOException {
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        MarkerOptions markerOptions = new MarkerOptions();
        Address address;
        address = geocoder.getFromLocationName(SharedPrefManager.getInstance(MapsActivity.this).getUser().getIndirizzo(), 1).get(0);
        markerOptions.position(new LatLng(address.getLatitude(),address.getLongitude()));
        markerOptions.title(getString(R.string.home));
        markerOptions.icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.marker_home));
        mGoogleMap.addMarker(markerOptions);
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
                    return requestHandler.sendPostRequest(URLs.URL_REQUEST_RIDE, params);
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    //displaying the progress bar while user registers on the server
                    overlayLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    //hiding the progressbar after completion
                    overlayLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(s);

                        //if no error in response
                        if (!obj.getBoolean("error")) {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            markerPassaggio.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            btnRequest.setText(R.string.requested_ride);
                            btnRequest.setEnabled(false);
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
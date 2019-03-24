package it.uniba.di.sms.carpooling.Passaggio;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Locale;

import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.SharedPrefManager;
import it.uniba.di.sms.carpooling.Utente;

import static android.content.Context.LOCATION_SERVICE;


public class InfoPassaggioRichiestoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PASSAGGIO = "passaggio";

    // TODO: Rename and change types of parameters
    private Passaggio passaggioParam;

    ImageView callIcon;


    //MAP
    private MapView mMapView;
    private GoogleMap googleMap;
    private Utente utente = SharedPrefManager.getInstance(getActivity()).getUser();


    public InfoPassaggioRichiestoFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static InfoPassaggioRichiestoFragment newInstance(Passaggio passaggioParam) {
        InfoPassaggioRichiestoFragment fragment = new InfoPassaggioRichiestoFragment();
        Bundle args = new Bundle();
        args.putSerializable(PASSAGGIO, passaggioParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            passaggioParam = (Passaggio) getArguments().get(PASSAGGIO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_info_passaggio_richiesto, container, false);


        Toolbar toolbar = v.findViewById(R.id.my_toolbar);
        toolbar.setTitle("Informazioni sul passaggio");

        FloatingActionButton buttonTracking = v.findViewById(R.id.floatStartTracking);
        buttonTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check whether GPS tracking is enabled//
                LocationManager lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(getActivity(), "GPS disattivato. Non puoi utilizzare questa funzione.", Toast.LENGTH_SHORT).show();
                    //getActivity().finish();
                }
                else if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startTrackerService();
                }
            }
        });

        callIcon = v.findViewById(R.id.call_icon);
        callIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + passaggioParam.getTelefonoAutista()));
                startActivity(callIntent);
            }
        });

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        setHasOptionsMenu(true);

        //delay loading of the map till fragment opened animation if finished
        if (googleMap == null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (isAdded()) {
                        mMapView = v.findViewById(R.id.map);
                        mMapView.onCreate(savedInstanceState);
                        mMapView.onResume();
                        try {
                            MapsInitializer.initialize(getActivity().getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mMapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap mMap) {
                                googleMap = mMap;
                                try {
                                    addAutistaMarker(passaggioParam, mMap);
                                    addMyMarker(mMap);
                                    addAziendaMarker(mMap);


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }, 400);
        }




        TextView autistaText = v.findViewById(R.id.autista);
        TextView telefonoText = v.findViewById(R.id.telefono);
        TextView autoText = v.findViewById(R.id.auto);
        TextView postiAutoText = v.findViewById(R.id.posti_auto);
        TextView confermatoText = v.findViewById(R.id.confermato);
        TextView dataText = v.findViewById(R.id.data);
        TextView direzioneText = v.findViewById(R.id.direzione);



        autistaText.append(": " + passaggioParam.getNomeAutista() + " " + passaggioParam.getCognomeAutista());
        telefonoText.append(": " + passaggioParam.getTelefonoAutista());
        autoText.append(": " + passaggioParam.getAutomobile());
        postiAutoText.append(": " + passaggioParam.getNumPosti());
        switch (passaggioParam.getConfermato()) {
            case 0:
                confermatoText.append(": " + getText(R.string.wait_conferm));
                break;
            case 1:
                confermatoText.append(": " + getText(R.string.confermed));
                break;
            case 2:
                confermatoText.append(": " + getText(R.string.rejected));
                break;
        }

        dataText.append(": " + passaggioParam.getData());
        if(passaggioParam.getDirezione()==0){
            direzioneText.append(": " + getText(R.string.one_way));
        }else{
            direzioneText.append(": " + getText(R.string.backHome));
        }

        //Show button if ride is started and not finished
        if(passaggioParam.isIniziato() && passaggioParam.getConcluso()==0){
            buttonTracking.setVisibility(View.VISIBLE);
        }

        return v;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void addAutistaMarker(Passaggio passaggio, GoogleMap map) throws IOException {
        Address autistaAddress = null;
        MarkerOptions markerOptions = new MarkerOptions();
        Marker marker;
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        //crea marker dall'indirizzo dell'autista
        autistaAddress = geocoder.getFromLocationName(passaggioParam.getIndirizzo(), 1).get(0);
        LatLng position = new LatLng(autistaAddress.getLatitude(),autistaAddress.getLongitude());

        markerOptions.position(position);
        markerOptions.title(passaggioParam.getNomeAutista() + " " + passaggioParam.getCognomeAutista());
        markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.marker_car));
        marker = map.addMarker(markerOptions);
        marker.setTag(passaggioParam);
    }


    private void addMyMarker(GoogleMap map) throws IOException {
        Address myAddress = null;
        MarkerOptions markerOptions = new MarkerOptions();
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        myAddress = geocoder.getFromLocationName(utente.getIndirizzo(), 1).get(0);
        LatLng position = new LatLng(myAddress.getLatitude(),myAddress.getLongitude());


        markerOptions.position(position);
        markerOptions.title(getString(R.string.home));
        markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.marker_home));
        map.addMarker(markerOptions);
        // For zooming functionality
        CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(13).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void addAziendaMarker(GoogleMap map) throws IOException {
        Address aziendaAddress = null;
        MarkerOptions markerOptions = new MarkerOptions();
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        aziendaAddress = geocoder.getFromLocationName(utente.getIndirizzoAzienda(), 1).get(0);
        LatLng position = new LatLng(aziendaAddress.getLatitude(),aziendaAddress.getLongitude());

        markerOptions.position(position);
        markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.marker_factory));
        markerOptions.title(utente.getAzienda());
        map.addMarker(markerOptions);
    }

    //Start the TrackerService//

    private void startTrackerService() {
        Intent serviceIntent = new Intent(getActivity(), TrackingService.class);
        serviceIntent.putExtra("id_passaggio",passaggioParam.getId());
        serviceIntent.putExtra("passenger",true);
        getActivity().startService(serviceIntent);
    }


}

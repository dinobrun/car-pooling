package it.uniba.di.sms.carpooling.Passaggio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


public class InfoPassaggioRichiestoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PASSAGGIO = "passaggio";

    // TODO: Rename and change types of parameters
    private Passaggio passaggioParam;
    CardView card;


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
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info_passaggio_richiesto, container, false);
        //card = v.findViewById(R.id.info_richiesto);

        Toast.makeText(getActivity(),"richiesto",Toast.LENGTH_SHORT).show();

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

        TextView autistaText = v.findViewById(R.id.autista);
        TextView autoText = v.findViewById(R.id.auto);
        TextView dataText = v.findViewById(R.id.data);
        TextView direzioneText = v.findViewById(R.id.direzione);


        autistaText.append(" " + passaggioParam.getAutista());
        autoText.append(" " + passaggioParam.getAutomobile());
        dataText.append(" " + passaggioParam.getData());
        direzioneText.append(" " + passaggioParam.getDirezione());

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
        markerOptions.title(passaggioParam.getAutista());
        //markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_car));
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
        markerOptions.title(utente.getUsername());
        //markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.home_icon));
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
        markerOptions.title(utente.getAzienda());
        //markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.work_icon));
        map.addMarker(markerOptions);
    }

}

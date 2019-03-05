package it.uniba.di.sms.carpooling.Passaggio;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Locale;

import it.uniba.di.sms.carpooling.R;


public class InfoPassaggioRichiestoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PASSAGGIO = "passaggio";

    // TODO: Rename and change types of parameters
    private Passaggio passaggioParam;


    //MAP
    private MapView mMapView;
    private GoogleMap googleMap;


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

                MarkerOptions markerOptions = new MarkerOptions();
                Marker marker;
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                Address address = null;
                try {
                    address = geocoder.getFromLocationName(passaggioParam.getIndirizzo(), 1).get(0);
                    LatLng position = new LatLng(address.getLatitude(),address.getLongitude());

                    markerOptions.position(position);
                    if(passaggioParam.isRichiesto()){
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    }
                    markerOptions.title(passaggioParam.getAutista());
                    marker = mMap.addMarker(markerOptions);
                    marker.setTag(passaggioParam);

                    // For zooming functionality
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        TextView autistaText = v.findViewById(R.id.autista);
        TextView autoText = v.findViewById(R.id.auto);
        TextView dataText = v.findViewById(R.id.data);
        TextView direzioneText = v.findViewById(R.id.direzione);

        Button annullaPassaggio = v.findViewById(R.id.annullaPassaggio);

        autistaText.append(" " + passaggioParam.getAutista());
        autoText.append(" " + passaggioParam.getAutomobile());
        dataText.append(" " + passaggioParam.getData());
        direzioneText.append(" " + passaggioParam.getDirezione());

        annullaPassaggio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Aggiungi codice quando si annulla la richiesta di un passaggio
            }
        });
        return v;
    }




}

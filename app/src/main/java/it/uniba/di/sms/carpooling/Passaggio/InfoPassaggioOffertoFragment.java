package it.uniba.di.sms.carpooling.Passaggio;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.RequestHandler;
import it.uniba.di.sms.carpooling.URLs;
import it.uniba.di.sms.carpooling.Utente;


public class InfoPassaggioOffertoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PASSAGGIO = "passaggio";
    private static final String UTENTI = "utenti";

    // TODO: Rename and change types of parameters
    private Passaggio passaggioParam;
    private ArrayList<Utente> utentiParam;


    //MAP
    private MapView mMapView;
    private GoogleMap googleMap;

    CardView card;
    ImageView close;
    TextView txtNome;
    TextView txtCognome;
    TextView txtTelefono;
    Button btnAccept;
    Button btnDecline;

    public InfoPassaggioOffertoFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static InfoPassaggioOffertoFragment newInstance(Passaggio passaggioParam, ArrayList<Utente> utentiParam) {
        InfoPassaggioOffertoFragment fragment = new InfoPassaggioOffertoFragment();
        Bundle args = new Bundle();
        args.putSerializable(PASSAGGIO, passaggioParam);
        args.putSerializable(UTENTI,utentiParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            passaggioParam = (Passaggio) getArguments().get(PASSAGGIO);
            utentiParam = (ArrayList<Utente>) getArguments().get(UTENTI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info_passaggio_offerto, container, false);

        txtNome = v.findViewById(R.id.txtNome);
        txtCognome = v.findViewById(R.id.txtCognome);
        txtTelefono =  v.findViewById(R.id.txtTelefono);
        btnAccept =  v.findViewById(R.id.btnAccept);
        btnDecline =  v.findViewById(R.id.btnDecline);
        card = v.findViewById(R.id.info);
        close = v.findViewById(R.id.close_card);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.setVisibility(View.INVISIBLE);
            }
        });

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

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        displayInfo(marker);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), (float) 14));
                        return true;
                    }
                });
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

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), (float) 14));
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //Aggiunge i marker degli utenti che hanno richiesto il passaggio
                for(Utente u : utentiParam) {
                    try {
                        addMarkerUtenti(u);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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




    private void addMarkerUtenti(Utente utente) throws IOException {
        MarkerOptions markerOptions = new MarkerOptions();
        Marker marker;
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        Address address = geocoder.getFromLocationName(utente.getIndirizzo(), 1).get(0);
        markerOptions.position(new LatLng(address.getLatitude(),address.getLongitude()));
        markerOptions.title(utente.getUsername());

        if(utente.isConfermato()){
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }

        marker = googleMap.addMarker(markerOptions);
        marker.setTag(utente);
    }

    //crea un riquadro in cui inserisce le info del marker
    public void displayInfo(final Marker marker) {
        //visualizza la card con le informazioni sul passaggio
        card.setVisibility(View.VISIBLE);

        final Utente utente = (Utente) marker.getTag();
        txtNome.setText(utente.getNome());
        txtCognome.setText(utente.getCognome());
        txtTelefono.setText(utente.getTelefono());

        //accetta il passaggio
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptDeclinePassaggio(passaggioParam.getId(),utente.getUsername(), true);
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }
        });

        //rifiuta il passaggio
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptDeclinePassaggio(passaggioParam.getId(),utente.getUsername(), false);
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            }
        });
    }

    //Metodo asincrono che accetta o rifiuta il passaggio
    private void acceptDeclinePassaggio(final int idPassaggio, final String usernameRichiedente, final Boolean conferma) {

        //if it passes all the validations
        class AcceptDeclinePassaggio extends AsyncTask<Void, Void, String> {


            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("ID", Integer.toString(idPassaggio));
                params.put("Username", usernameRichiedente);
                params.put("Conferma", conferma.toString());

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_ACCEPT_DECLINE_PASSAGGIO, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try{
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {

                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        AcceptDeclinePassaggio up = new AcceptDeclinePassaggio();
        up.execute();
    }



}

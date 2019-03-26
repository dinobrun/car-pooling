package it.uniba.di.sms.carpooling.Passaggio;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.RequestHandler;
import it.uniba.di.sms.carpooling.SharedPrefManager;
import it.uniba.di.sms.carpooling.URLs;
import it.uniba.di.sms.carpooling.Utente;

import static android.content.Context.LOCATION_SERVICE;


public class InfoPassaggioOffertoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PASSAGGIO = "passaggio";
    private static final String UTENTI = "utenti";

    // TODO: Rename and change types of parameters
    private Passaggio passaggioParam;


    //MAP
    private MapView mMapView;
    private GoogleMap googleMap;
    private Utente user = SharedPrefManager.getInstance(getActivity()).getUser();

    CardView card;
    ImageView close;
    TextView txtNome;
    TextView txtCognome;
    TextView txtTelefono;
    Button btnAccept;
    Button btnDecline;
    ImageView callIcon;

    private static final int PERMISSIONS_REQUEST = 100;

    public InfoPassaggioOffertoFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static InfoPassaggioOffertoFragment newInstance(Passaggio passaggioParam) {
        InfoPassaggioOffertoFragment fragment = new InfoPassaggioOffertoFragment();
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
        final View v = inflater.inflate(R.layout.fragment_info_passaggio_offerto, container, false);

        //toolbar
        Toolbar toolbar = v.findViewById(R.id.my_toolbar);
        toolbar.setTitle(R.string.ride_info);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        setHasOptionsMenu(true);

        Button startTracking = v.findViewById(R.id.btnStartTracking);
        startTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check whether GPS tracking is enabled//
                LocationManager lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(getActivity(), R.string.gps_disabled, Toast.LENGTH_SHORT).show();
                    //getActivity().finish();
                }
                else if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startTrackerService();
                }
            }
        });

        //show start button if ride is not finished
        if(passaggioParam.getConcluso()==0){
            startTracking.setEnabled(true);
        }

        txtNome = v.findViewById(R.id.txtNome);
        txtCognome = v.findViewById(R.id.txtCognome);
        txtTelefono =  v.findViewById(R.id.txtTelefono);
        btnAccept =  v.findViewById(R.id.btnAccept);
        btnDecline =  v.findViewById(R.id.btnDecline);
        card = v.findViewById(R.id.info);
        close = v.findViewById(R.id.close_card);
        callIcon = v.findViewById(R.id.call_icon);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.setVisibility(View.INVISIBLE);
            }
        });

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
                                MarkerOptions markerOptions = new MarkerOptions();
                                Marker marker;
                                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                Address address = null;

                                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        if(marker.getTag() != null){
                                            displayInfo(marker);
                                            return true;
                                        }else {
                                            return false;
                                        }
                                    }
                                });
                                try {
                                    //crea marker sull'indirizzo dell'utente che ha offerto il passaggio
                                    setUserMarker(googleMap);
                                    addAziendaMarker(googleMap);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                getUtentiPassages(passaggioParam);

                            }
                        });

                    }
                }
            }, 400);
        }


        TextView autoText = v.findViewById(R.id.auto);
        TextView dataText = v.findViewById(R.id.data);
        TextView direzioneText = v.findViewById(R.id.direzione);

        autoText.append(": " + passaggioParam.getAutomobile());

        //set data in correct format
        // First convert the String to a Date
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ITALIAN);
        Date date = null;
        try {
            date = dateParser.parse(passaggioParam.getData());
            // Then convert the Date to a String, formatted as you dd/MM/yyyy
            SimpleDateFormat dateFormatter = new SimpleDateFormat("E d MMM yyyy HH:mm", Locale.ITALY);
            dataText.append(": " + dateFormatter.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(passaggioParam.getDirezione()==0){
            direzioneText.append(": " + getText(R.string.one_way));
        }else{
            direzioneText.append(": " + getText(R.string.backHome));
        }

        return v;
    }


    //funzione che crea il marker sull'indirizzo dell'utente che richiede il passaggio
    private void setUserMarker(GoogleMap mGoogleMap) throws IOException {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        MarkerOptions markerOptions = new MarkerOptions();
        Address address;
        address = geocoder.getFromLocationName(SharedPrefManager.getInstance(getActivity()).getUser().getIndirizzo(), 1).get(0);
        markerOptions.position(new LatLng(address.getLatitude(),address.getLongitude()));
        markerOptions.title(getString(R.string.home));
        markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.marker_home));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), (float) 14));
        mGoogleMap.addMarker(markerOptions);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void addAziendaMarker(GoogleMap map) throws IOException {
        Address aziendaAddress = null;
        MarkerOptions markerOptions = new MarkerOptions();
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        aziendaAddress = geocoder.getFromLocationName(user.getIndirizzoAzienda(), 1).get(0);
        LatLng position = new LatLng(aziendaAddress.getLatitude(),aziendaAddress.getLongitude());

        markerOptions.position(position);
        markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.marker_factory));
        markerOptions.title(user.getAzienda());
        map.addMarker(markerOptions);
    }

    private void addMarkerUtenti(Utente utente) throws IOException {
        MarkerOptions markerOptions = new MarkerOptions();
        Marker marker;
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        Address address = geocoder.getFromLocationName(utente.getIndirizzo(), 1).get(0);
        markerOptions.position(new LatLng(address.getLatitude(),address.getLongitude()));
        markerOptions.title(utente.getUsername());

        switch (utente.getConfermato()){
            //confermato
            case 1: markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            break;
            //rifiutato
            case 2: markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            break;
            //in sospeso
            case 0: markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            break;
            //se c'è un errore
            default: markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }
        marker = googleMap.addMarker(markerOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), (float) 14));
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

        if(((Utente) marker.getTag()).getConfermato()==1){
            btnAccept.setEnabled(false);
        }else if(((Utente) marker.getTag()).getConfermato()==2){
            btnDecline.setEnabled(false);
        }

        //accetta il passaggio
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptDeclinePassaggio(passaggioParam.getId(), marker, true);
            }
        });

        //rifiuta il passaggio
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptDeclinePassaggio(passaggioParam.getId(), marker, false);
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            }
        });

        callIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + utente.getTelefono()));
                startActivity(callIntent);
            }
        });
    }

    //Metodo asincrono che accetta o rifiuta il passaggio
    private void acceptDeclinePassaggio(final int idPassaggio, final Marker utenteRichiedente, final Boolean conferma) {

        //if it passes all the validations
        class AcceptDeclinePassaggio extends AsyncTask<Void, Void, String> {

            Utente utente = (Utente) utenteRichiedente.getTag();
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("ID", Integer.toString(idPassaggio));
                params.put("Username", utente.getUsername());
                params.put("Conferma", conferma.toString());

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_ACCEPT_DECLINE_RIDE, params);
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
                        if(conferma){
                            utente.setConfermato(1);
                            utenteRichiedente.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            btnAccept.setEnabled(false);
                            btnDecline.setEnabled(true);
                            Toast.makeText(getActivity(), R.string.accepted_request, Toast.LENGTH_SHORT).show();
                        }else{
                            utente.setConfermato(2);
                            utenteRichiedente.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                            btnDecline.setEnabled(false);
                            btnAccept.setEnabled(true);
                            Toast.makeText(getActivity(), R.string.rejected_request, Toast.LENGTH_SHORT).show();
                        }



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

    //restituisce la lista di utenti che hanno richiesto il passaggio
    private void getUtentiPassages(final Passaggio passaggio) {

        //if it passes all the validations
        class UtentiPassages extends AsyncTask<Void, Void, String> {

            ArrayList<Utente> listaUtentiPassages = new ArrayList<>();

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("ID", Integer.toString(passaggio.getId()));

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_GET_LIST_USER_REQUESTED, params);
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

                        //if list is not empty
                        if(!obj.getBoolean("empty_list")){

                            JSONArray utentiJson = obj.getJSONArray("utente");

                            for(int i=0; i<utentiJson.length(); i++){
                                JSONObject temp = utentiJson.getJSONObject(i);
                                listaUtentiPassages.add(new Utente(
                                        temp.getString("username"),
                                        temp.getString("nome"),
                                        temp.getString("cognome"),
                                        temp.getString("indirizzo"),
                                        temp.getString("email"),
                                        temp.getString("telefono"),
                                        Integer.parseInt(temp.getString("confermato"))
                                ));
                            }
                            //Aggiunge i marker degli utenti che hanno richiesto il passaggio
                            for(Utente u : listaUtentiPassages) {
                                try {
                                    addMarkerUtenti(u);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else{
                            Toast.makeText(getActivity(), R.string.nobody_request, Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        //executing the async task
        UtentiPassages up = new UtentiPassages();
        up.execute();
    }







    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {

        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //startTrackerService();
        } else {

            //If the user denies the permission request, then display a toast with some more information//
            Toast.makeText(getActivity(), R.string.gps_enabled, Toast.LENGTH_SHORT).show();
        }
    }

//Start the TrackerService//

    private void startTrackerService() {
        Intent serviceIntent = new Intent(getActivity(), TrackingService.class);
        serviceIntent.putExtra("id_passaggio",passaggioParam.getId());
        serviceIntent.putExtra("passenger",false);
        getActivity().startService(serviceIntent);
    }




}

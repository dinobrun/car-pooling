package it.uniba.di.sms.carpooling;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CercaPassaggioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CercaPassaggioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CercaPassaggioFragment extends Fragment implements Serializable {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DATA = "data";
    private static final String DIREZIONE = "direzione";
    private static final String AZIENDA = "user";

    // TODO: Rename and change types of parameters
    private String dataParam;
    private String direzioneParam;
    private String aziendaParam;

    private OnFragmentInteractionListener mListener;


    public CercaPassaggioFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CercaPassaggioFragment newInstance(String data, String direzione, String azienda) {
        CercaPassaggioFragment fragment = new CercaPassaggioFragment();
        Bundle args = new Bundle();
        args.putString(DATA, data);
        args.putString(DIREZIONE, direzione);
        args.putString(AZIENDA, azienda);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dataParam = getArguments().getString(DATA);
            direzioneParam = getArguments().getString(DIREZIONE);
            aziendaParam = getArguments().getString(AZIENDA);
        }
        getPassaggi();
    }

   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_cerca_passaggio, container, false);
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);



        Toast.makeText(getActivity(),dataParam,Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(),direzioneParam,Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(),aziendaParam,Toast.LENGTH_SHORT).show();


        getPassaggi();

        return v;
    }*/

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
                params.put("Username", SharedPrefManager.getInstance(getContext()).getUser().getUsername());
                params.put("Data", dataParam);
                params.put("Azienda", aziendaParam);
                params.put("Direzione", direzioneParam);

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

                //Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();

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
                                        aziendaParam,
                                        temp.getString("direzione"),
                                        Integer.parseInt(temp.getString("num_posti"))
                                ));
                                //Toast.makeText(getActivity(), Integer.parseInt(temp.getString("id")), Toast.LENGTH_SHORT).show();
                            }
                            //lista di passaggi già richiesti
                            JSONArray passaggi_richiesti = obj.getJSONArray("passaggio_utente");
                            ArrayList<String> passaggi_ID = new ArrayList<>();
                            for(int i=0; i<passaggi_richiesti.length(); i++){
                                JSONObject temp = passaggi_richiesti.getJSONObject(i);
                                passaggi_ID.add(temp.getString("ID"));
                            }


                            //passa all'activity mappa inserendo due liste
                            Intent mapIntent = new Intent(getActivity(), MapsActivity.class);
                            mapIntent.putExtra("Passaggi",listaPassaggi);
                            mapIntent.putExtra("Passaggi_utente", passaggi_ID);
                            startActivity(mapIntent);

                        }
                        else{
                            Toast.makeText(getActivity(), "Lista vuota scemo", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }





                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        GetPassaggio pg = new GetPassaggio();
        pg.execute();
    }


    public void provaStampa(ArrayList<Passaggio> lista){
        Toast.makeText(getActivity(), lista.get(0).getIndirizzo(), Toast.LENGTH_SHORT).show();
    }


}

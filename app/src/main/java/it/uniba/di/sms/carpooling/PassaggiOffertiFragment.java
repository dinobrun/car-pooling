package it.uniba.di.sms.carpooling;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class PassaggiOffertiFragment extends Fragment {

    //the recyclerview
    RecyclerView recyclerView;

    String user = SharedPrefManager.getInstance(getActivity()).getUser().getUsername();


    public PassaggiOffertiFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_passaggi_offerti, container, false);




//getting the recyclerview from xml
        recyclerView = v.findViewById(R.id.recyclerOfferti);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        getListPassages();


        return v;
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



    private void getListPassages() {

        //if it passes all the validations
        class ListPassages extends AsyncTask<Void, Void, String> {

            ArrayList<Passaggio> listaPassaggi = new ArrayList<>();
            String aziendaParam = SharedPrefManager.getInstance(getContext()).getUser().getUsername();

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Username", user);

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_GET_OFFERED_PASSAGES, params);
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

                try{

                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();

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
                                        user,
                                        temp.getString("indirizzo"),
                                        temp.getString("data"),
                                        temp.getString("automobile"),
                                        aziendaParam,
                                        temp.getString("direzione"),
                                        Integer.parseInt(temp.getString("num_posti"))
                                ));
                                //Toast.makeText(getActivity(), Integer.parseInt(temp.getString("id")), Toast.LENGTH_SHORT).show();
                            }

                            //creating recyclerview adapter
                            PassaggioAdapter adapter = new PassaggioAdapter(getActivity(), listaPassaggi);

                            //setting adapter to recyclerview
                            recyclerView.setAdapter(adapter);


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

        //executing the async task
        ListPassages lp = new ListPassages();
        lp.execute();
    }
}

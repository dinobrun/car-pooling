package it.uniba.di.sms.carpooling.Automobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.RequestHandler;
import it.uniba.di.sms.carpooling.SharedPrefManager;
import it.uniba.di.sms.carpooling.SwipeHelper;
import it.uniba.di.sms.carpooling.URLs;


public class CarListFragment extends Fragment {

    String autoNameInput;
    int numPostiInput;
    String username;
    RecyclerView recyclerView;
    CarAdapter adapter;
    ArrayList<Automobile> listaAutomobiliUltima = new ArrayList<>();

    RelativeLayout overlayLayout;
    ProgressBar progressBar;

    public CarListFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CarListFragment newInstance() {
        CarListFragment fragment = new CarListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_car_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_car:
                showAddAutoPopup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        username = SharedPrefManager.getInstance(getActivity()).getUser().getUsername();


        View v = inflater.inflate(R.layout.fragment_lista_auto, container, false);

        overlayLayout = v.findViewById(R.id.overlayLayout);
        progressBar = v.findViewById(R.id.progressBar);



        //getting the recyclerview from xml
        recyclerView = v.findViewById(R.id.recycler_auto);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Toolbar toolbar = v.findViewById(R.id.my_toolbar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setTitle(R.string.my_cars);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        setHasOptionsMenu(true);


        //Restituisce le auto
        listaAutomobiliUltima = getAuto();



        SwipeHelper swipeHelper = new SwipeHelper(getActivity(), recyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "-",
                        R.drawable.add_ride_icon,
                        Color.parseColor("#FF3C30"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                deleteAuto(listaAutomobiliUltima.get(pos));
                            }
                        }
                ));

            }
        };
        return v;
    }

    public void showAddAutoPopup(){

        final AlertDialog.Builder builderNomeAuto = new AlertDialog.Builder(getActivity());
        builderNomeAuto.setTitle(R.string.add_car_name);
        builderNomeAuto.setMessage(R.string.car_name);

        // Set up the input
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        builderNomeAuto.setView(input);

        // Set up the buttons
        builderNomeAuto.setPositiveButton(R.string.forward, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(input.getText().toString().equals("")){
                    input.setError(getText(R.string.request_auto_name));
                    input.requestFocus();
                }
                else{
                    autoNameInput = input.getText().toString();

                    AlertDialog.Builder builderPostiAuto = new AlertDialog.Builder(getActivity());
                    builderPostiAuto.setTitle(R.string.add_seats);
                    builderPostiAuto.setMessage(R.string.num_seats);


                    // Set up the input
                    final EditText inputNumPosti = new EditText(getActivity());

                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    inputNumPosti.setInputType(InputType.TYPE_CLASS_NUMBER);
                    builderPostiAuto.setView(inputNumPosti);
                    builderPostiAuto.setPositiveButton(R.string.forward, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(inputNumPosti.getText().toString().equals("")){
                                inputNumPosti.setError(getText(R.string.request_auto_num_seats));
                                inputNumPosti.requestFocus();
                            } else{
                                numPostiInput = Integer.parseInt(inputNumPosti.getText().toString());
                                addAuto(autoNameInput,numPostiInput);
                            }
                        }
                    });
                    builderPostiAuto.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builderPostiAuto.show();
                }



            }
        });
        builderNomeAuto.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builderNomeAuto.show();
    }


    private void addAuto(final String nomeAuto, final int numPosti) {

        class Auto extends AsyncTask<Void, Void, String> {


            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Username", username);
                params.put("Nome", nomeAuto);
                params.put("Num_Posti", Integer.toString(numPosti));

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_ADD_CAR, params);
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
                        //Riavvia il fragment
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(CarListFragment.this).attach(CarListFragment.this).commit();

                    } else {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        Auto a = new Auto();
        a.execute();
    }



    //metodo che restituisce la lista di auto dell'utente
    private ArrayList<Automobile> getAuto(){

        final ArrayList<Automobile> automobili = new ArrayList<>();

        //classe per prendere le aziende
        class AutoDB extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {

                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Username", username);

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_GET_CARS, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
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

                        if(!obj.getBoolean("empty_list")){
                            JSONArray companyJson = obj.getJSONArray("automobile");
                            for(int i=0; i<companyJson.length(); i++){
                                JSONObject temp = companyJson.getJSONObject(i);
                                automobili.add(new Automobile(
                                        Integer.parseInt(temp.getString("id")),
                                        temp.getString("nome"),
                                        Integer.parseInt(temp.getString("num_posti")),
                                        username
                                ));
                            }
                        }

                        //creating recyclerview adapter
                        adapter = new CarAdapter(getActivity(), automobili);

                        //setting adapter to recyclerview
                        recyclerView.setAdapter(adapter);

                    } else {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        AutoDB autoDB = new AutoDB();
        autoDB.execute();
        return automobili;
    }


    //metodo che restituisce la lista di auto dell'utente
    private void deleteAuto(final Automobile auto) {

        //classe per prendere le aziende
        class AutoDB extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {

                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(auto.getId()));

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_DELETE_CAR, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {


                    //converting response to json object
                        JSONObject obj = new JSONObject(s);

                        //if no error in response
                        if (!obj.getBoolean("error")) {
                            listaAutomobiliUltima.remove(auto);
                            adapter.notifyDataSetChanged();
                        } else {
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), R.string.deleted_car_impossible, Toast.LENGTH_SHORT).show();
                        }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        AutoDB autoDB = new AutoDB();
        autoDB.execute();

    }

}

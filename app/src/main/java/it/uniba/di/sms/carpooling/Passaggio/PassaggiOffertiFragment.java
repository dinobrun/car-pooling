package it.uniba.di.sms.carpooling.Passaggio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.RecyclerItemClickListener;
import it.uniba.di.sms.carpooling.RequestHandler;
import it.uniba.di.sms.carpooling.SharedPrefManager;
import it.uniba.di.sms.carpooling.URLs;
import it.uniba.di.sms.carpooling.Utente;


public class PassaggiOffertiFragment extends Fragment implements ActionMode.Callback, SwipeRefreshLayout.OnRefreshListener {

    //the recyclerview
    private RecyclerView recyclerView;
    private PassaggioOffertoAdapter adapter;

    private ActionMode actionMode;
    private boolean isMultiSelect = false;
    //i created List of int type to store id of data, you can create custom class type data according to your need.
    private List<Integer> selectedIds = new ArrayList<>();
    SwipeRefreshLayout mSwipeRefreshLayout;

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

        // SwipeRefreshLayout
        mSwipeRefreshLayout = v.findViewById(R.id.swipe_container_offerti);
        mSwipeRefreshLayout.setOnRefreshListener(PassaggiOffertiFragment.this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        //getting the recyclerview from xml
        recyclerView = v.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                getListPassages();
            }
        });


//getting the recyclerview from xml
        recyclerView = v.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //getListPassages();

        return v;
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {

        // Fetching data from server
        getListPassages();
    }

    private void multiSelect(int position) {
        Passaggio data = adapter.getItem(position);
        if (data != null){
            if (actionMode != null) {
                if (selectedIds.contains(data.getId()))
                    selectedIds.remove(Integer.valueOf(data.getId()));
                else
                    selectedIds.add(data.getId());
                if (selectedIds.size() > 0)
                    actionMode.setTitle("selezionati: " + String.valueOf(selectedIds.size())); //show selected item count on action mode.
                else{
                    actionMode.setTitle(""); //remove item count from action mode.
                    actionMode.finish();
                    actionMode = null; //hide action mode.
                }
                adapter.setSelectedIds(selectedIds);

            }
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.remove_item, menu);
        return true;
    }


    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.remove_item:
                showopup();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        isMultiSelect = false;
        selectedIds = new ArrayList<>();
        adapter.setSelectedIds(new ArrayList<Integer>());
    }


    private void getListPassages() {
        mSwipeRefreshLayout.setRefreshing(true);

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
                                        Integer.parseInt(temp.getString("direzione")),
                                        Integer.parseInt(temp.getString("num_posti")),
                                        (1==Integer.parseInt(temp.getString("concluso")))
                                ));
                                listaPassaggi.get(i).setRichiesteInSospeso(Integer.parseInt(temp.getString("passaggi_in_sospeso")));
                            }

                            //creating recyclerview adapter
                            adapter = new PassaggioOffertoAdapter(getActivity(), listaPassaggi);

                            //setting adapter to recyclerview
                            recyclerView.setAdapter(adapter);

                            //stop refreshing animation
                            mSwipeRefreshLayout.setRefreshing(false);

                            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    if (isMultiSelect) {
                                        //if multiple selection is enabled then select item on single click else perform normal click on item.
                                        multiSelect(position);
                                    }
                                    else{
                                        InfoPassaggioOffertoFragment fragment = InfoPassaggioOffertoFragment.newInstance(listaPassaggi.get(position));
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                                        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                                        transaction.addToBackStack(null);
                                        transaction.add(R.id.main_content, fragment, "BLANK_FRAGMENT").commit();
                                    }
                                }

                                @Override
                                public void onLongItemClick(View view, int position) {
                                    if (!isMultiSelect) {
                                        selectedIds = new ArrayList<>();
                                        isMultiSelect = true;
                                        if (actionMode == null) {
                                            actionMode = view.startActionMode(PassaggiOffertiFragment.this); //show ActionMode.
                                        }
                                    }
                                    multiSelect(position);
                                }
                            }));

                        }
                        else{
                            Toast.makeText(getActivity(), "Nessun passaggio offerto", Toast.LENGTH_SHORT).show();
                            //stop refreshing animation
                            mSwipeRefreshLayout.setRefreshing(false);
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






    public void showopup(){
        AlertDialog.Builder builderNomeAuto = new AlertDialog.Builder(getActivity());
        builderNomeAuto.setTitle("Sei sicuro?");
        builderNomeAuto.setMessage("Le modifiche apportate non potranno essere annullate");

        // Set up the buttons
        builderNomeAuto.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                detelePassaggi(selectedIds);
            //esegue le query di cancellazione

            }
        });
        builderNomeAuto.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builderNomeAuto.show();
    }



    //Metodo asincrono che cancella i passaggi selezionati
    private void detelePassaggi(final List<Integer> idPassaggi) {

        //if it passes all the validations
        class DeletePassaggi extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {

                JSONArray array = new JSONArray();

                for(Integer id : idPassaggi) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("id_passaggio", id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    array.put(obj);
                }

                JSONObject mainObj = new JSONObject();
                try {
                    mainObj.put("passaggi",array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Array_Passaggi", mainObj.toString());

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_DELETE_PASSAGGI, params);
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

                        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                        actionMode.finish();
                        actionMode = null; //hide action mode.

                        //Riavvia il fragment
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(PassaggiOffertiFragment.this).attach(PassaggiOffertiFragment.this).commit();


                    } else {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        DeletePassaggi dp = new DeletePassaggi();
        dp.execute();
    }



}

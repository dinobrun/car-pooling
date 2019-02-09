package it.uniba.di.sms.carpooling;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CompanyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String NOME = "nome";
    private static final String COGNOME = "cognome";
    private static final String INDIRIZZO = "indirizzo";
    private static final String TELEFONO = "telefono";
    private static final String DATANASCITA = "dataNascita";

    // TODO: Rename and change types of parameters
    private String usernameParam;
    private String passwordParam;
    private String emailParam;
    private String nomeParam;
    private String cognomeParam;
    private String indirizzoParam;
    private String telefonoParam;
    private String dataNascitaParam;

    private String company = null;

    private ArrayList<String> companies;



    //Costruttore
    public CompanyFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static CompanyFragment newInstance(String username, String password, String email, String nome, String cognome, String indirizzo, String telefono, String dataNascita) {
        CompanyFragment fragment = new CompanyFragment();
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        args.putString(PASSWORD, password);
        args.putString(EMAIL, email);
        args.putString(NOME, nome);
        args.putString(COGNOME, cognome);
        args.putString(INDIRIZZO, indirizzo);
        args.putString(TELEFONO, telefono);
        args.putString(DATANASCITA, dataNascita);
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            usernameParam = getArguments().getString(USERNAME);
            passwordParam = getArguments().getString(PASSWORD);
            emailParam = getArguments().getString(EMAIL);
            nomeParam = getArguments().getString(NOME);
            cognomeParam = getArguments().getString(COGNOME);
            indirizzoParam = getArguments().getString(INDIRIZZO);
            telefonoParam = getArguments().getString(TELEFONO);
            dataNascitaParam = getArguments().getString(DATANASCITA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_company, container, false);

        //restituisce la lista delle aziende presenti nel DB
        companies = getCompanies();

        //spinner
        final Spinner spinner = v.findViewById(R.id.spinner3);

        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, companies){
            @Override
            public boolean isEnabled(int position) {
                if(position==0){
                    return false;
                }
                return true;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                TextView txt = (TextView) v;
                if(position==0){
                    txt.setTextColor(Color.GRAY);
                }
                else{
                    txt.setTextColor(Color.BLACK);
                }
                return v;
            }
        };

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        adapter.add("Seleziona la tua azienda");

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=0){
                    company = adapter.getItem(i);
                    Toast.makeText(getActivity(), company, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button registratiBtn = v.findViewById(R.id.registrati);
        registratiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        // Inflate the layout for this fragment
        return v;

    }




    private ArrayList<String> getCompanies(){

        final ArrayList<String> aziende = new ArrayList<>();

        //classe per prendere le aziende
        class Companies extends AsyncTask<Void, Void, String> {


            @Override
            protected String doInBackground(Void... voids) {

                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                return requestHandler.sendGetRequest(URLs.URL_GETCOMPANIES);
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
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();


                    JSONObject obj = new JSONObject(s);


                    //if no error in response
                    if (!obj.getBoolean("error")) {

                        JSONArray companyJson = obj.getJSONArray("azienda");

                        for(int i=0; i<companyJson.length(); i++){
                            JSONObject temp = companyJson.getJSONObject(i);
                            aziende.add(temp.getString("nome"));
                        }


                    } else {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }

        Companies companies = new Companies();
        companies.execute();

        return aziende;
    }


    private void registerUser() {

        //if it passes all the validations

        class RegisterUser extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Username", usernameParam);
                params.put("Password", passwordParam);
                params.put("Nome", nomeParam);
                params.put("Cognome", cognomeParam);
                params.put("DataNascita", dataNascitaParam);
                params.put("Indirizzo", indirizzoParam);
                params.put("Email", emailParam);
                params.put("Telefono", telefonoParam);
                params.put("Azienda",company);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_REGISTER, params);
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

                try {
                    //converting response to json object
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                    JSONObject obj = new JSONObject(s);


                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new user object
                        Utente user = new Utente(
                                userJson.getString("username"),
                                userJson.getString("nome"),
                                userJson.getString("cognome"),
                                userJson.getString("indirizzo"),
                                userJson.getString("email"),
                                userJson.getString("telefono"),
                                userJson.getString("dataNascita")
                        );

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getActivity()).userLogin(user);

                        //starting the profile activity
                        Toast.makeText(getActivity(), "tutto apposto", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        RegisterUser ru = new RegisterUser();
        ru.execute();
    }



}





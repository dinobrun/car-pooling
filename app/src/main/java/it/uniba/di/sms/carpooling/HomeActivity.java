package it.uniba.di.sms.carpooling;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class HomeActivity extends FragmentActivity implements CreaPassaggioFragment.OnFragmentInteractionListener, CercaPassaggioFragment.OnFragmentInteractionListener {

    final String user = SharedPrefManager.getInstance(HomeActivity.this).getUser().getUsername();
    private String autoName = "";
    private String direzioneSelected;
    private String aziendaUtente;
    private Date time;
    RadioButton rd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        aziendaUtente = SharedPrefManager.getInstance(HomeActivity.this).getUser().getAzienda();

        //Radio group andata/ritorno
        RadioGroup rg = findViewById(R.id.radioGroup);
        rg.check(R.id.radioButtonAndata);
        rd = findViewById(R.id.radioButtonAndata);
        direzioneSelected=rd.getText().toString();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rd = findViewById(checkedId);
                direzioneSelected=rd.getText().toString();
            }
        });

        final TimePicker timePicker =  findViewById(R.id.time_picker);
        final DatePicker datePicker = findViewById(R.id.date_picker);
        final Calendar calendar = new GregorianCalendar();

        Button cercaPassaggioBtn = findViewById(R.id.cercaPassaggio);
        cercaPassaggioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.set(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getHour(),
                        timePicker.getMinute());

                time = calendar.getTime();
                openCercaPassaggioFragment();
            }
        });



        Button creaPassaggioBtn = findViewById(R.id.creaPassaggio);
        creaPassaggioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAuto();
            }
        });

        Button logout = findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(HomeActivity.this).logout();
                Intent openLogin = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(openLogin);
            }
        });


        Button btnMyPassages= findViewById(R.id.buttonMyPassages);
        btnMyPassages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openMieiPassaggiFragment(SharedPrefManager.getInstance(HomeActivity.this).getUser().getUsername());
                Intent openPassaggi = new Intent(HomeActivity.this,PassaggiActivity.class);
                startActivity(openPassaggi);

            }
        });


    }

    //Apre CreaPassaggioFragment
    public void openCreaPassaggioFragment(ArrayList<Automobile> automobili) {
        CreaPassaggioFragment fragment = CreaPassaggioFragment.newInstance(automobili);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.open_frag, fragment, "BLANK_FRAGMENT").commit();
    }

    //Apre CercaPassaggioFragment
    public void openCercaPassaggioFragment() {
        CercaPassaggioFragment fragment = CercaPassaggioFragment.newInstance(new java.sql.Timestamp(time.getTime()).toString(),direzioneSelected,aziendaUtente);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.open_frag, fragment, "BLANK_FRAGMENT").commit();
    }

    //Apre MieiPassaggiFragment
    public void openMieiPassaggiFragment(String username) {
        PassaggiRichiestiFragment fragment = new PassaggiRichiestiFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.open_frag, fragment, "BLANK_FRAGMENT").commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }


    //metodo che restituisce la lista di auto dell'utente prima di creare un passaggio
    private void getAuto(){

        final ArrayList<Automobile> automobili = new ArrayList<>();

        //classe per prendere le aziende
        class AutoDB extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {

                //user conterrà l'username dell'utente in sessione
                String user = SharedPrefManager.getInstance(getApplicationContext()).getUser().getUsername();

                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Username", user);

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_GETAUTO, params);
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

                        JSONArray companyJson = obj.getJSONArray("automobile");
                            for(int i=0; i<companyJson.length(); i++){
                            JSONObject temp = companyJson.getJSONObject(i);
                            automobili.add(new Automobile(
                                    temp.getString("nome"),
                                    Integer.parseInt(temp.getString("num_posti")),
                                    temp.getString("id_utente")
                            ));
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                    //Se l'utente non ha auto aggiunte
                    if(automobili.isEmpty()){
                        //Popup per aggiungere una auto
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("Aspetta!");
                        builder.setMessage("Non hai aggiunto ancora automobili. Non puoi offrire un passaggio. Vuoi aggiungerne una?");
                        builder.setPositiveButton("Aggiungi",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                            //Aprire fragment per aggiungere una auto
                                        //openAddAutoFragment();
                                        showAddAutoPopup();
                                    }
                                });
                        builder.setNegativeButton("Non ora", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    //Se l'utente possiede almeno una auto
                    else{
                        //Toast.makeText(HomeActivity.this, automobili.get(0).getNome(), Toast.LENGTH_SHORT).show();
                        openCreaPassaggioFragment(automobili);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        AutoDB autoDB = new AutoDB();
        autoDB.execute();

    }


    public void showAddAutoPopup(){

        /*
        final ArrayList<String> automobili = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Aggiungi una auto");
        builder.setMessage("Inserisci il nome dell'auto da aggiungere");

        // Set up the input
        final EditText input = new EditText(HomeActivity.this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Avanti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                autoName = input.getText().toString();
                addAuto();
                automobili.add(autoName);
                openCreaPassaggioFragment(automobili);
            }
        });

        builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
*/
    }



    private void addAuto() {

        class Auto extends AsyncTask<Void, Void, String> {


            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Username", user);
                params.put("Nome", autoName);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_ADDAUTO, params);
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
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(HomeActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(HomeActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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



}




package it.uniba.di.sms.carpooling;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class HomeActivity extends FragmentActivity implements CreaPassaggioFragment.OnFragmentInteractionListener, AddAutoFragment.OnFragmentInteractionListener {

    String response;
    final String user = SharedPrefManager.getInstance(HomeActivity.this).getUser().getUsername();

    private static final String TAG = "MainActivity";

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private Button creaPassaggioBtn;

    private FrameLayout creaPassaggioContainer;

    private String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mDisplayDate = findViewById(R.id.tvDate);

        creaPassaggioContainer = findViewById(R.id.crea_passaggio_frag);

        creaPassaggioBtn = findViewById(R.id.creaPassaggio);
        creaPassaggioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Controlla se l'utente ha almeno una auto. Se sì, avvia il fragment successivo,
                // altrimenti apre il popup per aggiungerne una.
                getAuto();

            }
        });

        //Seleziona la data desiderata
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        HomeActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,day,month);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
            }
        };
    }



    //Apre CreaPassaggioFragment
    public void openCreaPassaggioFragment(ArrayList<String> automobili) {
        CreaPassaggioFragment fragment = CreaPassaggioFragment.newInstance(automobili);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.crea_passaggio_frag, fragment, "BLANK_FRAGMENT").commit();
    }

    //Apre AddAutoFragment
    public void openAddAutoFragment() {
        AddAutoFragment fragment = new AddAutoFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.add_auto_frag, fragment, "BLANK_FRAGMENT").commit();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }




    private void getAuto(){

        final ArrayList<String> automobili = new ArrayList<>();

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
                            automobili.add(temp.getString("nome"));
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

        final ArrayList<String> automobili = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Aggiungi una auto");
        builder.setMessage("Inserisci il nome dell'auto da aggiungere");

        // Set up the input
        final EditText input = new EditText(HomeActivity.this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Avanti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                automobili.add(m_Text);
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

    }



}




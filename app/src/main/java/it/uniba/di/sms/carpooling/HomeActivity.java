package it.uniba.di.sms.carpooling;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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

import it.uniba.di.sms.carpooling.Accesso.LoginActivity;
import it.uniba.di.sms.carpooling.Automobile.Automobile;
import it.uniba.di.sms.carpooling.Automobile.ListaAutoFragment;
import it.uniba.di.sms.carpooling.Passaggio.CercaPassaggioFragment;
import it.uniba.di.sms.carpooling.Passaggio.CreaPassaggioFragment;
import it.uniba.di.sms.carpooling.Passaggio.PassaggiActivity;
import it.uniba.di.sms.carpooling.Passaggio.PassaggiRichiestiFragment;

public class HomeActivity extends AppCompatActivity implements CreaPassaggioFragment.OnFragmentInteractionListener, CercaPassaggioFragment.OnFragmentInteractionListener {

    private String user;
    private String autoName = "";
    private String direzioneSelected;
    private String aziendaUtente;
    private Date time;
    RadioButton rd;
    private DrawerLayout drawerLayout;


    Toolbar myToolbar;

    //Inizializzazione variabili Inserimento data
    int year, month, day, hour, minute;
    Calendar calendar = new GregorianCalendar();
    DatePickerDialog.OnDateSetListener mDateSetListener = null;
    TimePickerDialog.OnTimeSetListener mTimeSetListener = null;
    Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //user conterrà l'username dell'utente in sessione
        user = SharedPrefManager.getInstance(getApplicationContext()).getUser().getUsername();


        drawerLayout = findViewById(R.id.drawer_layout);


        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.nav_camera:
                                Intent openPassaggi = new Intent(HomeActivity.this,PassaggiActivity.class);
                                startActivity(openPassaggi);
                                break;

                            case R.id.auto_section:
                                openListaAutoFragment();
                                break;

                            case R.id.logout_section:
                                SharedPrefManager.getInstance(HomeActivity.this).logout();
                                Intent openLogin = new Intent(HomeActivity.this, LoginActivity.class);
                                openLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                openLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(openLogin);
                                finish();
                                break;

                        }

                        // set item as selected to persist highlight
                        menuItem.setChecked(true);

                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        return true;
                    }
                });


        ///////////////

        //Inserimento data
        final TextView dataText = findViewById(R.id.dataTextHome);
        final TextView orarioText = findViewById(R.id.orarioTextHome);

        //Set current date
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR);
        minute = cal.get(Calendar.MINUTE);
        dataText.setText(day + "/" + (month+1) + "/" + year);
        orarioText.setText(cal.get(Calendar.HOUR) + " : " + cal.get(Calendar.MINUTE));


        dataText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(HomeActivity.this,android.R.style.Theme_Holo_Dialog, mDateSetListener, year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int yearParam, int monthParam, int dayParam) {
                year = yearParam;
                month=monthParam;
                day=dayParam;
                String date = day + "/" + (month+1) + "/" + year;
                dataText.setText(date);

            }
        };

        orarioText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hour=cal.get(Calendar.HOUR);
                minute=cal.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(HomeActivity.this,android.R.style.Theme_Holo_Dialog,mTimeSetListener,hour,minute,true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourParam, int minuteParam) {
                hour=hourParam;
                minute=minuteParam;
                String time = hour + " : " + minute;
                orarioText.setText(time);
            }
        };

        /////////////////




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



        Button cercaPassaggioBtn = findViewById(R.id.cercaPassaggio);
        cercaPassaggioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.set(year,
                        month,
                        day,
                        hour,
                        minute);

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


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    //Apre ListaAutoFragment
    public void openListaAutoFragment() {
        ListaAutoFragment fragment = new ListaAutoFragment();
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


    //metodo che restituisce la lista di auto dell'utente
    private void getAuto(){

        final ArrayList<Automobile> automobili = new ArrayList<>();

        //classe per prendere le aziende
        class AutoDB extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {

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

                        if(!obj.getBoolean("empty_list")){
                            JSONArray companyJson = obj.getJSONArray("automobile");
                            for(int i=0; i<companyJson.length(); i++){
                                JSONObject temp = companyJson.getJSONObject(i);
                                automobili.add(new Automobile(
                                        Integer.parseInt(temp.getString("id")),
                                        temp.getString("nome"),
                                        Integer.parseInt(temp.getString("num_posti")),
                                        user
                                ));
                            }
                        }
                        else {
                            //Apre popup per aggiungere auto
                        }

                    } else {
                        Toast.makeText(HomeActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    //Se l'utente non ha auto aggiunte
                    if(automobili.isEmpty()){
                        //
                    }
                    //Se l'utente possiede almeno una auto
                    else {
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

}




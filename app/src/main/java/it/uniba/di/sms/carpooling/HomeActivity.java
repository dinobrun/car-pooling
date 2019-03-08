package it.uniba.di.sms.carpooling;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
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

public class HomeActivity extends AppCompatActivity implements CreaPassaggioFragment.OnFragmentInteractionListener{


    private String user;
    private String direzioneSelected;
    private String aziendaUtente;
    RadioButton rd;
    private DrawerLayout drawerLayout;

    ImageView profPic;
    Toolbar myToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //user conterrà l'username dell'utente in sessione
        user = SharedPrefManager.getInstance(getApplicationContext()).getUser().getUsername();


        drawerLayout = findViewById(R.id.drawer_layout);

        profPic= findViewById(R.id.immagineProva);


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
                                Intent openPassaggi = new Intent(HomeActivity.this, PassaggiActivity.class);
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

        aziendaUtente = SharedPrefManager.getInstance(HomeActivity.this).getUser().getAzienda();

        Button cercaPassaggioBtn = findViewById(R.id.cercaPassaggio);
        cercaPassaggioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        getProfilePicture();


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
        CercaPassaggioFragment fragment = new CercaPassaggioFragment();
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
                        Toast.makeText(HomeActivity.this, "Aggiungi prima una automobile.", Toast.LENGTH_SHORT).show();
                    }
                    //Se l'utente possiede almeno una auto
                    else {

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





    //metodo che restituisce la foto dell'utente
    private void getProfilePicture(){

        //classe per prendere le aziende
        class ProfilePicture extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {

                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Username", user);

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_GET_PROFILE_PICTURE, params);
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
                            byte[] decodedString = Base64.decode(obj.getString("foto"), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            profPic.setImageBitmap(decodedByte);


                        }
                        else {
                            //Apre popup per aggiungere auto
                        }

                    } else {
                        Toast.makeText(HomeActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        ProfilePicture pp = new ProfilePicture();
        pp.execute();

    }



}




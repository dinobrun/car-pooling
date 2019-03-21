package it.uniba.di.sms.carpooling;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

import it.uniba.di.sms.carpooling.Accesso.LoginActivity;
import it.uniba.di.sms.carpooling.Automobile.Automobile;
import it.uniba.di.sms.carpooling.Automobile.ListaAutoFragment;
import it.uniba.di.sms.carpooling.Passaggio.CercaPassaggioFragment;
import it.uniba.di.sms.carpooling.Passaggio.CreaPassaggioFragment;
import it.uniba.di.sms.carpooling.Passaggio.PassaggiActivity;

public class HomeActivity extends AppCompatActivity implements CreaPassaggioFragment.OnFragmentInteractionListener{


    private String user;
    private DrawerLayout drawerLayout;
    Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if user is not authorized
        if(SharedPrefManager.getInstance(HomeActivity.this).getUser().getAutorizzato()==0){
            setContentView(R.layout.fragment_not_accepted);
            user = SharedPrefManager.getInstance(getApplicationContext()).getUser().getUsername();
            drawerLayout = findViewById(R.id.home_layout);

            myToolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);


            NavigationView navigationView = findViewById(R.id.nav_view_not_accepted);
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {

                            switch (menuItem.getItemId()) {
                                case R.id.my_profile:
                                    openMyProfileFragment();
                                    break;

                                case R.id.logout_section:
                                    logout();
                                    break;
                            }

                            // set item as selected to persist highlight
                            menuItem.setChecked(true);
                            // close drawer when item is tapped
                            drawerLayout.closeDrawers();
                            return true;
                        }
                    });
        //if user is authorized
        }else{
            setContentView(R.layout.activity_home);

            //get the logged username
            user = SharedPrefManager.getInstance(getApplicationContext()).getUser().getUsername();

            drawerLayout = findViewById(R.id.home_layout);

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
                                case R.id.rides_section:
                                    Intent openMyRides = new Intent(HomeActivity.this, PassaggiActivity.class);
                                    startActivity(openMyRides);
                                    break;

                                case R.id.car_section:
                                    openCarListFragment();
                                    break;

                                case R.id.my_profile_section:
                                    openMyProfileFragment();
                                    break;

                                case R.id.logout_section:
                                    logout();
                                    break;

                            }

                            // set item as selected to persist highlight
                            menuItem.setChecked(true);

                            // close drawer when item is tapped
                            drawerLayout.closeDrawers();
                            return true;
                        }
                    });

            //Button createRide
            FloatingActionButton createRide = findViewById(R.id.btnCreateRide);
            createRide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createRide();
                }
            });

            //Button findRide
            FloatingActionButton findRide = findViewById(R.id.btnFindRide);
            Animation searchRideAnimation=AnimationUtils.loadAnimation(this, R.anim.scale_animation);
            findRide.startAnimation(searchRideAnimation);
            findRide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFindRideFragment();
                }
            });
        }
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
    public void openCreateRideFragment(ArrayList<Automobile> automobili) {
        CreaPassaggioFragment fragment = CreaPassaggioFragment.newInstance(automobili);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.open_frag, fragment, "BLANK_FRAGMENT").commit();
    }


    //Apre CercaPassaggioFragment
    public void openFindRideFragment() {
        CercaPassaggioFragment fragment = new CercaPassaggioFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.open_frag, fragment, "BLANK_FRAGMENT").commit();
    }

    //Apre ListaAutoFragment
    public void openCarListFragment() {
        ListaAutoFragment fragment = new ListaAutoFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.open_frag, fragment, "BLANK_FRAGMENT").commit();
    }

    //Apre ListaAutoFragment
    public void openMyProfileFragment() {
        MioProfiloFragment fragment = new MioProfiloFragment();
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
    private void createRide(){

        final ArrayList<Automobile> cars = new ArrayList<>();

        class RideCreation extends AsyncTask<Void, Void, String> {

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
                                cars.add(new Automobile(
                                        Integer.parseInt(temp.getString("id")),
                                        temp.getString("nome"),
                                        Integer.parseInt(temp.getString("num_posti")),
                                        user
                                ));
                            }
                        }
                        else {
                            Toast.makeText(HomeActivity.this, "Devi prima aggiungere una auto", Toast.LENGTH_SHORT).show();
                            openCarListFragment();
                        }

                    } else {
                        Toast.makeText(HomeActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    //Se l'utente non ha auto aggiunte
                    if(cars.isEmpty()){
                        Toast.makeText(HomeActivity.this, R.string.add_auto, Toast.LENGTH_SHORT).show();
                        openCarListFragment();
                    }
                    //Se l'utente possiede almeno una auto
                    else {
                        openCreateRideFragment(cars);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        RideCreation rc = new RideCreation();
        rc.execute();

    }

    //metodo che effettua il logout impostando a null il token nel database
    private void logout(){


        class UpdateToken extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {

                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("ID_Utente", user);

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_UPDATE_TOKEN, params);
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
                        SharedPrefManager.getInstance(HomeActivity.this).logout();
                        Intent openLogin = new Intent(HomeActivity.this, LoginActivity.class);
                        openLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        openLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(openLogin);
                        finish();
                    } else {
                        Toast.makeText(HomeActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        UpdateToken updateToken = new UpdateToken();
        updateToken.execute();

    }


}

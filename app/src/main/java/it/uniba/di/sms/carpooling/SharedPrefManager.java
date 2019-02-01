package it.uniba.di.sms.carpooling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_NAME = "simplifiedcodingsharedpref";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_NOME = "keynome";
    private static final String KEY_COGNOME = "keycognome";
    private static final String KEY_DATANASCITA = "keydatanascita";
    private static final String KEY_SESSO = "keysesso";
    private static final String KEY_INDIRIZZO = "keyindirizzo";
    private static final String KEY_TELEFONO = "keytelefono";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(Utente user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_NOME, user.getNome());
        editor.putString(KEY_COGNOME, user.getCognome());
        editor.putString(KEY_DATANASCITA, user.getEmail());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_SESSO, user.getSesso());
        editor.putString(KEY_INDIRIZZO, user.getIndirizzo());
        editor.putString(KEY_TELEFONO, user.getTelefono());
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    //this method will give the logged in user
    public Utente getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Utente(
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_NOME, null),
                sharedPreferences.getString(KEY_COGNOME, null),
                sharedPreferences.getString(KEY_SESSO, null),
                sharedPreferences.getString(KEY_INDIRIZZO, null),
                sharedPreferences.getString(KEY_DATANASCITA, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_TELEFONO, null)
        );
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }

}
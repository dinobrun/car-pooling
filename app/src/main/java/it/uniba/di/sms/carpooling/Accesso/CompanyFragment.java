package it.uniba.di.sms.carpooling.Accesso;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.RequestHandler;
import it.uniba.di.sms.carpooling.URLs;

import static android.app.Activity.RESULT_OK;

public class CompanyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    ImageView profilePhoto;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String NAME = "name";
    private static final String SURNAME = "surname";
    private static final String ADDRESS = "address";
    private static final String TELEPHONE = "telephone";
    private static final String BIRTHDATE = "birthDate";

    // TODO: Rename and change types of parameters
    private String usernameParam;
    private String passwordParam;
    private String emailParam;
    private String nameParam;
    private String surnameParam;
    private String addressParam;
    private String telephoneParam;
    private String birthDateParam;
    private String company = null;
    private String stringImage = "null";
    private ProgressBar progressBar;
    Spinner companySpinner;

    //Costruttore
    public CompanyFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static CompanyFragment newInstance(String username, String password, String email, String name, String surname, String address, String telephone, String birthDate) {
        CompanyFragment fragment = new CompanyFragment();
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        args.putString(PASSWORD, password);
        args.putString(EMAIL, email);
        args.putString(NAME, name);
        args.putString(SURNAME, surname);
        args.putString(ADDRESS, address);
        args.putString(TELEPHONE, telephone);
        args.putString(BIRTHDATE, birthDate);
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
            nameParam = getArguments().getString(NAME);
            surnameParam = getArguments().getString(SURNAME);
            addressParam = getArguments().getString(ADDRESS);
            telephoneParam = getArguments().getString(TELEPHONE);
            birthDateParam = getArguments().getString(BIRTHDATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_company, container, false);

        Toolbar toolbar = v.findViewById(R.id.my_toolbar_2);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        setHasOptionsMenu(true);


        progressBar=v.findViewById(R.id.progressBarCompany);
        //get the list of the companies
        ArrayList<String> companies = getCompanies();

        //spinner
        companySpinner = v.findViewById(R.id.companySpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, companies){
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
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

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        companySpinner.setAdapter(adapter);
        adapter.add(getString(R.string.select_company));
        companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=0){
                    company = adapter.getItem(i);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        //profile image
        profilePhoto = v.findViewById(R.id.imageProfile);
        Button takePhoto = v.findViewById(R.id.takePhotoButton);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getText(R.string.select_photo)), 1);
            }
        });

        Button registerButton = v.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        return v;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK && requestCode== 1 && data != null && data.getData() != null){
            Uri filepath = data.getData();
            try {
                Bitmap profileBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath);
                profilePhoto.setImageBitmap(profileBitmap);
                stringImage = getStringImage(profileBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //convert from bitmap to string
    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
    }

    //get the companies list
    private ArrayList<String> getCompanies(){

        final ArrayList<String> listCompanies = new ArrayList<>();

        class Companies extends AsyncTask<Void, Void, String> {


            @Override
            protected String doInBackground(Void... voids) {

                RequestHandler requestHandler = new RequestHandler();
                return requestHandler.sendGetRequest();
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {

                        JSONArray companyJson = obj.getJSONArray("azienda");

                        for(int i=0; i<companyJson.length(); i++){
                            JSONObject temp = companyJson.getJSONObject(i);
                            listCompanies.add(temp.getString("nome"));
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
        return listCompanies;
    }

    private void setSpinnerError(Spinner spinner, String error){
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            spinner.requestFocus();
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError("error"); // any name of the error will do
            selectedTextView.setTextColor(Color.RED); //text color in which you want your error message to be displayed
            selectedTextView.setText(error); // actual error message
            spinner.performClick(); // to open the spinner list if error is found.

        }
    }

    private void registerUser() {
            if(companySpinner.getSelectedItem().toString().equals(getString(R.string.select_company))){
                Toast.makeText(getActivity(), R.string.choose_company, Toast.LENGTH_SHORT).show();
            }

        class RegisterUser extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Username", usernameParam);
                params.put("Password", passwordParam);
                params.put("Nome", nameParam);
                params.put("Cognome", surnameParam);
                params.put("DataNascita", birthDateParam);
                params.put("Indirizzo", addressParam);
                params.put("Email", emailParam);
                params.put("Telefono", telephoneParam);
                params.put("Azienda",company);
                params.put("Immagine", stringImage);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_REGISTER, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //displaying the progress bar while user registers on the server
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                progressBar.setVisibility(View.GONE);

                try {


                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getActivity(), R.string.successful_registration, Toast.LENGTH_SHORT).show();
                        //back to LoginActivity
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        RegisterUser ru = new RegisterUser();
        ru.execute();
    }


}





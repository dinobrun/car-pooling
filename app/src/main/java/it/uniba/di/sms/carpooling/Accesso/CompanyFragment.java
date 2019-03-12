package it.uniba.di.sms.carpooling.Accesso;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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
import it.uniba.di.sms.carpooling.SharedPrefManager;
import it.uniba.di.sms.carpooling.URLs;
import it.uniba.di.sms.carpooling.Utente;

import static android.app.Activity.RESULT_OK;

public class CompanyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final int IMAGE_PICK_CODE=1000;
    ImageView profilePhoto;

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
    private Bitmap bitmap;
    private String stringImage = "null";
    private ProgressBar progressBar;

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

        Toolbar toolbar = v.findViewById(R.id.my_toolbar_2);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.home_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        progressBar=v.findViewById(R.id.progressBarCompany);
        //restituisce la lista delle aziende presenti nel DB
        companies = getCompanies();

        //spinner
        final Spinner spinner = v.findViewById(R.id.spinner3);

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
        spinner.setAdapter(adapter);
        adapter.add("Seleziona la tua azienda");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


        //immagine del profilo
        profilePhoto = v.findViewById(R.id.imageProfile);
        Button takePhoto = v.findViewById(R.id.takePhotoButton);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleziona una foto"), 1);
            }
        });

        Button registratiBtn = v.findViewById(R.id.registrati);
        registratiBtn.setOnClickListener(new View.OnClickListener() {
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
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath);
                profilePhoto.setImageBitmap(bitmap);
                stringImage = getStringImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //prende in input un bitmap e lo converte in una stringa
    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
        return encodedImage;
    }

    private ArrayList<String> getCompanies(){

        final ArrayList<String> aziende = new ArrayList<>();

        //classe per prendere le aziende
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

        class RegisterUser extends AsyncTask<Void, Void, String> {

            /*
            //Converte l'imageview in un bitmap
            BitmapDrawable imageDrawable=  (BitmapDrawable) profilePhoto.getDrawable();
            Bitmap imageBitmap=imageDrawable.getBitmap();
*/

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                /*bitmap = BitmapFactory.decodeFile(imageUri.getPath());
                //Converte il bitmap in una stringa
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                */

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
                    Toast.makeText(getActivity(), "Account registrato con successo", Toast.LENGTH_SHORT).show();
                    //torna alla LoginActivity
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        JSONObject userJson = obj.getJSONObject("user");

                        Utente user = new Utente(
                                userJson.getString("username"),
                                userJson.getString("nome"),
                                userJson.getString("cognome"),
                                userJson.getString("indirizzo"),
                                userJson.getString("email"),
                                userJson.getString("telefono"),
                                userJson.getString("dataNascita")
                        );

                        if(!userJson.getString("azienda").equals("")){
                            user.addAzienda(userJson.getString("azienda"));
                        }
                        SharedPrefManager.getInstance(getActivity()).userLogin(user);
                        Toast.makeText(getActivity(), "tutto apposto", Toast.LENGTH_SHORT).show();

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


    //metodo per arrotondare gli angoli di un bitmap
    public static Bitmap roundCorner(Bitmap src, float round)
    {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();

        // create bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // set canvas for painting
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        // config paint
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        // config rectangle for embedding
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        // draw rect to canvas
        canvas.drawRoundRect(rectF, round, round, paint);

        // create Xfer mode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // draw source image to canvas
        canvas.drawBitmap(src, rect, rect, paint);

        // return final image
        return result;
    }

}





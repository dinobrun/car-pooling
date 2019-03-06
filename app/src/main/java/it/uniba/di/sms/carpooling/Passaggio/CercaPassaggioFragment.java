package it.uniba.di.sms.carpooling;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import it.uniba.di.sms.carpooling.Passaggio.Passaggio;


public class CercaPassaggioFragment extends Fragment implements Serializable {


    private String aziendaUtente;
    private String direzioneSelected;
    private Date time;
    RadioButton rd;

    //Inizializzazione variabili Inserimento data
    int year, month, day, hour, minute;
    Calendar calendar = new GregorianCalendar();
    DatePickerDialog.OnDateSetListener mDateSetListener = null;
    TimePickerDialog.OnTimeSetListener mTimeSetListener = null;
    Calendar cal = Calendar.getInstance();

    public CercaPassaggioFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CercaPassaggioFragment newInstance() {
        CercaPassaggioFragment fragment = new CercaPassaggioFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_cerca_passaggio, container, false);

       Toolbar toolbar = v.findViewById(R.id.my_toolbar);

       ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
       toolbar.setNavigationIcon(R.drawable.back_icon);
       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               getActivity().onBackPressed();
           }
       });


       //Inserimento data
       final TextView dataText = v.findViewById(R.id.dataText);
       final TextView orarioText = v.findViewById(R.id.orarioText);


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
               DatePickerDialog dialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Dialog, mDateSetListener, year,month,day);
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
               TimePickerDialog dialog = new TimePickerDialog(getActivity(),android.R.style.Theme_Holo_Dialog,mTimeSetListener,hour,minute,true);
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




       aziendaUtente = SharedPrefManager.getInstance(getActivity()).getUser().getAzienda();

       //Radio group andata/ritorno
       RadioGroup rg = v.findViewById(R.id.radioGroup);
       rg.check(R.id.radioButtonAndata);
       rd = v.findViewById(R.id.radioButtonAndata);
       direzioneSelected=rd.getText().toString();

       rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
           public void onCheckedChanged(RadioGroup group, int checkedId) {
               rd = v.findViewById(checkedId);
               direzioneSelected=rd.getText().toString();
           }
       });



       Button cercaPassaggioBtn = v.findViewById(R.id.cercaPassaggio);
       cercaPassaggioBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               calendar.set(year,
                       month,
                       day,
                       hour,
                       minute);

               time = calendar.getTime();
               //openCercaPassaggioFragment();
               getPassaggi();
           }
       });


       return v;
    }





    //METODO CHE RESTITUISCE I PASSAGGI
    private void getPassaggi() {

        //if it passes all the validations
        class GetPassaggio extends AsyncTask<Void, Void, String> {

            //private ProgressBar progressBar;
            private final ArrayList<Passaggio> listaPassaggi = new ArrayList<>();

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Username", SharedPrefManager.getInstance(getContext()).getUser().getUsername());
                params.put("Data", new java.sql.Timestamp(time.getTime()).toString());
                params.put("Azienda", aziendaUtente);
                params.put("Direzione", direzioneSelected);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_GETPASSAGGI, params);
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
                                        temp.getString("nome"),
                                        temp.getString("indirizzo"),
                                        temp.getString("data"),
                                        temp.getString("automobile"),
                                        aziendaUtente,
                                        temp.getString("direzione"),
                                        Integer.parseInt(temp.getString("num_posti"))
                                ));
                            }
                            //lista di passaggi già richiesti
                            ArrayList<String> passaggi_ID = new ArrayList<>();
                            if(obj.getString("passaggio_utente") != "null"){
                            JSONArray passaggi_richiesti = obj.getJSONArray("passaggio_utente");
                                for(int i=0; i<passaggi_richiesti.length(); i++){
                                    JSONObject temp = passaggi_richiesti.getJSONObject(i);
                                    passaggi_ID.add(temp.getString("ID"));
                                }
                            }


                            //passa all'activity mappa inserendo due liste
                            Intent mapIntent = new Intent(getActivity(), MapsActivity.class);
                            mapIntent.putExtra("Passaggi",listaPassaggi);
                            mapIntent.putExtra("Passaggi_utente", passaggi_ID);
                            startActivity(mapIntent);

                        }
                        else{
                            Toast.makeText(getActivity(), "Nessun passaggio disponibile", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        GetPassaggio pg = new GetPassaggio();
        pg.execute();
    }






}

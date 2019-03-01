package it.uniba.di.sms.carpooling;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import static android.R.layout.simple_list_item_1;
import static android.R.layout.simple_list_item_2;

public class CreaPassaggioFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String AUTO = "auto";
    private ArrayList<Automobile> autoParam;
    private Date time;
    String value = "";
    Button mostra;
    private String selectionAuto = null;
    private Integer selectionPostoAuto;

    int year, month, day, hour, minute;

    Spinner spinnerAuto;
    Spinner spinnerPostiAuto;

    Calendar calendar = new GregorianCalendar();


    DatePickerDialog.OnDateSetListener mDateSetListener = null;
    TimePickerDialog.OnTimeSetListener mTimeSetListener = null;

    Calendar cal = Calendar.getInstance();


    public CreaPassaggioFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CreaPassaggioFragment newInstance(ArrayList<Automobile> automobiles) {
        CreaPassaggioFragment fragment = new CreaPassaggioFragment();
        Bundle args = new Bundle();
        args.putSerializable(AUTO,automobiles);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            autoParam = (ArrayList<Automobile>) getArguments().get(AUTO);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_scrolling, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addPassaggio:

                calendar.set(year,
                        month,
                        day,
                        hour,
                        minute);

                time = calendar.getTime();

                //Aggiunge il passaggio al DB
                addPassaggio();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_crea_passaggio, container, false);

        Toolbar toolbar = v.findViewById(R.id.my_toolbar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        setHasOptionsMenu(true);

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



        //Contiene i nomi delle auto
        ArrayList<String> nomiAuto = new ArrayList<>();
        for(Automobile a : autoParam)
            nomiAuto.add(a.getNome());


        //Spinner lista auto
        spinnerAuto = v.findViewById(R.id.spinnerAuto);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), simple_list_item_1, nomiAuto);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAuto.setAdapter(adapter);
        spinnerAuto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectionAuto = adapter.getItem(i);
                spinnerPostiAuto.setSelection(autoParam.get(i).getNumPosti()-2);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        //Spinner posti auto
        spinnerPostiAuto = v.findViewById(R.id.spinnerPostiAuto);
        final ArrayAdapter<String> adapterPostiAuto = new ArrayAdapter<>(this.getActivity(), simple_list_item_1, getResources().getStringArray(R.array.postiAutoList));
        adapterPostiAuto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPostiAuto.setAdapter(adapterPostiAuto);
        spinnerPostiAuto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectionPostoAuto = Integer.parseInt(adapterPostiAuto.getItem(i));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        //Inizializzo il radio group con il check su "Andata"
        RadioGroup rg = v.findViewById(R.id.radioGroup);
        rg.check(R.id.radioButtonAndata);
        RadioButton rd = v.findViewById(R.id.radioButtonAndata);
        value=rd.getText().toString();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rd = v.findViewById(checkedId);
                value=rd.getText().toString();
            }
        });


        return v;
    }





    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    ////INVIA AL SERVER LE INFO SUL PASSAGGIO
    private void addPassaggio() {

        final Utente utente = SharedPrefManager.getInstance(getActivity()).getUser();

        class Passaggio extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getActivity(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    } else {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("Autista", utente.getUsername());
                params.put("Data", new java.sql.Timestamp(time.getTime()).toString());
                params.put("Automobile",selectionAuto);
                params.put("Azienda",utente.getAzienda());
                params.put("Num_Posti",selectionPostoAuto.toString());
                params.put("Direzione",value);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_ADDPASSAGGIO, params);
            }
        }

        Passaggio p = new Passaggio();
        p.execute();
    }
}

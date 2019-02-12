package it.uniba.di.sms.carpooling;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static android.R.layout.simple_list_item_1;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreaPassaggioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreaPassaggioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreaPassaggioFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private static final String AUTO = "auto";

    private ArrayList<String> autoParam;

    private Date time;

    String value = "";

    String inputNameAuto = "";

    Button mostra;

    private String selectionAuto = null;

    private Integer selectionPostoAuto;

    public CreaPassaggioFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CreaPassaggioFragment newInstance(ArrayList<String> automobiles) {
        CreaPassaggioFragment fragment = new CreaPassaggioFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(AUTO,automobiles);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            autoParam = getArguments().getStringArrayList(AUTO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_crea_passaggio, container, false);

        final TimePicker timePicker =  v.findViewById(R.id.time_picker);

        final DatePicker datePicker = v.findViewById(R.id.date_picker);

        final Calendar calendar = new GregorianCalendar();

        mostra = v.findViewById(R.id.mostra);

        mostra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.set(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getHour(),
                        timePicker.getMinute());

                time = calendar.getTime();


                //Toast.makeText(getActivity(), time.toString(),Toast.LENGTH_SHORT).show();

                addPassaggio();

            }
        });

//spinner nomi auto
        final Spinner spinnerAuto = v.findViewById(R.id.spinnerAuto);

        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), simple_list_item_1, autoParam);


// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerAuto.setAdapter(adapter);


        spinnerAuto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectionAuto = adapter.getItem(i);
                if(selectionAuto.equals("Aggiungi auto")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Inserisci il nome dell'auto");

// Set up the input
                    final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

// Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            inputNameAuto = input.getText().toString();
                            adapter.add(inputNameAuto);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        adapter.add("Aggiungi auto");






        //Spinner posti auto
        //spinner
        final Spinner spinnerPostiAuto = v.findViewById(R.id.spinnerPostiAuto);

        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<String> adapterPostiAuto = new ArrayAdapter<String>(this.getActivity(), simple_list_item_1, getResources().getStringArray(R.array.spinnerItems));

// Specify the layout to use when the list of choices appears
        adapterPostiAuto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        spinnerPostiAuto.setAdapter(adapterPostiAuto);

        //adapterPostiAuto.add("Seleziona la tua azienda");

        spinnerPostiAuto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectionPostoAuto = Integer.parseInt(adapterPostiAuto.getItem(i));
                Toast.makeText(getActivity(), adapterPostiAuto.getItem(i),Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        RadioGroup rg = v.findViewById(R.id.radioGroup);
        rg.check(R.id.radioButtonAndata);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rd = v.findViewById(checkedId);
                value=rd.getText().toString();
            }
        });



        // Inflate the layout for this fragment
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

            //  ProgressBar progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //progressBar = (ProgressBar) findViewById(R.id.progressBar);
                // progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //progressBar.setVisibility(View.GONE);

                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        Intent goToHomeActivity = new Intent(getActivity(),HomeActivity.class);
                        startActivity(goToHomeActivity);
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

package it.uniba.di.sms.carpooling;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

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

                addPassaggio();

            }
        });

        final Spinner spinnerAuto = v.findViewById(R.id.spinnerAuto);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), simple_list_item_1, autoParam);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAuto.setAdapter(adapter);
        spinnerAuto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectionAuto = adapter.getItem(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //Spinner posti auto
        final Spinner spinnerPostiAuto = v.findViewById(R.id.spinnerPostiAuto);
        final ArrayAdapter<String> adapterPostiAuto = new ArrayAdapter<String>(this.getActivity(), simple_list_item_1, getResources().getStringArray(R.array.spinnerItems));
        adapterPostiAuto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPostiAuto.setAdapter(adapterPostiAuto);
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

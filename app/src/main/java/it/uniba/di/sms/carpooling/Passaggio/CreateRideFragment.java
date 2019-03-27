package it.uniba.di.sms.carpooling.Passaggio;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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

import it.uniba.di.sms.carpooling.Automobile.Automobile;
import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.RequestHandler;
import it.uniba.di.sms.carpooling.SharedPrefManager;
import it.uniba.di.sms.carpooling.URLs;
import it.uniba.di.sms.carpooling.Utente;

import static android.R.layout.simple_list_item_1;

public class CreateRideFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String CAR = "auto";
    private ArrayList<Automobile> carParam;
    private Date time;
    int direction = 0;
    private RadioButton ra;
    private RadioButton rr;
    private String selectionCars = null;
    private Integer selectionCarPlaces;
    int year, month, day, hour, minute;
    Spinner spinneCars;
    Spinner spinnerCarPlaces;
    int carID = 3;
    Calendar calendar = new GregorianCalendar();
    RelativeLayout overlayLayout;
    ProgressBar progressBar;

    DatePickerDialog.OnDateSetListener mDateSetListener = null;
    TimePickerDialog.OnTimeSetListener mTimeSetListener = null;

    Calendar cal = Calendar.getInstance();


    public CreateRideFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CreateRideFragment newInstance(ArrayList<Automobile> cars) {
        CreateRideFragment fragment = new CreateRideFragment();
        Bundle args = new Bundle();
        args.putSerializable(CAR,cars);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            carParam = (ArrayList<Automobile>) getArguments().get(CAR);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_scrolling, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addRide:

                calendar.set(year,
                        month,
                        day,
                        hour,
                        minute);

                time = calendar.getTime();

                for(Automobile car : carParam) {
                    if(car.getNome().equals(selectionCars)) {
                        carID = car.getId();
                    }
                }

                addRide();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_create_ride, container, false);

        Toolbar toolbar = v.findViewById(R.id.my_toolbar);
        toolbar.setTitle(R.string.create_ride);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        overlayLayout = v.findViewById(R.id.overlayLayout);
        progressBar = v.findViewById(R.id.progressBar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        setHasOptionsMenu(true);

        //date insert
        final TextView textDate = v.findViewById(R.id.textDate);
        final TextView textHour = v.findViewById(R.id.textHour);

        //Set current date
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        textDate.setText(day + "/" + (month+1) + "/" + year);
        textHour.setText(cal.get(Calendar.HOUR_OF_DAY) + " : " + cal.get(Calendar.MINUTE));


        textDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                textDate.setText(date);

            }
        };

        textHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                textHour.setText(time);
            }
        };


        //Contiene i nomi delle auto
        ArrayList<String> carNames = new ArrayList<>();
        for(Automobile car : carParam)
            carNames.add(car.getNome());


        //Spinner lista auto
        spinneCars = v.findViewById(R.id.spinnerCars);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), simple_list_item_1, carNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinneCars.setAdapter(adapter);
        spinneCars.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectionCars = adapter.getItem(i);
                spinnerCarPlaces.setSelection(carParam.get(i).getNumPosti()-2);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerCarPlaces = v.findViewById(R.id.spinnerCarPlaces);
        final ArrayAdapter<String> adapterPostiAuto = new ArrayAdapter<>(this.getActivity(), simple_list_item_1, getResources().getStringArray(R.array.postiAutoList));
        adapterPostiAuto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCarPlaces.setAdapter(adapterPostiAuto);
        spinnerCarPlaces.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectionCarPlaces = Integer.parseInt(adapterPostiAuto.getItem(i));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //start with the check on "One way"
        RadioGroup rg = v.findViewById(R.id.radioGroup);
        rg.check(R.id.radioButtonOneWay);
        ra = v.findViewById(R.id.radioButtonOneWay);
        rr = v.findViewById(R.id.radioButtonReturn);
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

    //add ride to DB
    private void addRide() {

        final Utente user = SharedPrefManager.getInstance(getActivity()).getUser();

        class Passaggio extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //displaying the progress bar while user registers on the server
                overlayLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                overlayLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getActivity(),R.string.new_ride_created,Toast.LENGTH_SHORT).show();
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

                 if(ra.isChecked()){
                     direction =0;
                 }else if(rr.isChecked()){
                     direction =1;
                 }

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                    params.put("Autista", user.getUsername());
                    params.put("Data", new java.sql.Timestamp(time.getTime()).toString());
                    params.put("Automobile",Integer.toString(carID));
                    params.put("Azienda",user.getAzienda());
                    params.put("Num_Posti", selectionCarPlaces.toString());
                    params.put("Direzione",Integer.toString(direction));

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_ADD_RIDE, params);
            }
        }

        Passaggio p = new Passaggio();
        p.execute();
    }
}

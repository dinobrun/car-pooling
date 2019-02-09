package it.uniba.di.sms.carpooling;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


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

    private Date time;

    Button mostra;

    public CreaPassaggioFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CreaPassaggioFragment newInstance() {
        CreaPassaggioFragment fragment = new CreaPassaggioFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crea_passaggio, container, false);

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

                Toast.makeText(getActivity(), time.toString(),Toast.LENGTH_SHORT).show();
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
}

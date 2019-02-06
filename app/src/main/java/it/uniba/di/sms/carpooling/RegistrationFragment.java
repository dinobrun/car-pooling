package it.uniba.di.sms.carpooling;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegistrationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationFragment extends Fragment implements CompanyFragment.OnFragmentInteractionListener {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";


    private Button avantiButton2;



    private EditText txtNome;
    private EditText txtCognome;
    private EditText txtDataNascita;
    private EditText txtIndirizzo;
    private EditText txtTelefono;


    private FrameLayout companyFragment;


    // TODO: Rename and change types of parameters
    private String usernameParam;
    private String passwordParam;
    private String emailParam;


    private OnFragmentInteractionListener mListener;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param username Parameter 1.
     * @param password Parameter 2.
     * @param email Parameter 3.
     * @return A new instance of fragment RegistrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationFragment newInstance(String username, String password, String email) {
        RegistrationFragment fragment = new RegistrationFragment();
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        args.putString(PASSWORD, password);
        args.putString(EMAIL, email);
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
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registration, container, false);




        txtNome = view.findViewById(R.id.nomeText);
        txtCognome = view.findViewById(R.id.cognomeText);
        txtIndirizzo = view.findViewById(R.id.indirizzoText);
        txtTelefono = view.findViewById(R.id.telefonoText);
        txtDataNascita = view.findViewById(R.id.dataNascitaText);

        //fragment
        companyFragment = view.findViewById(R.id.company_fragment);

        //avantiButton2 = view.findViewById(R.id.avantiButton2);

        /*avantiButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                secondRegistrationPart();
            }
        });*/


        // Inflate the layout for this fragment
        return view;
    }

    public void openFragment2(String nome, String cognome, String indirizzo, String telefono,String dataNascita) {

        CompanyFragment fragment = CompanyFragment.newInstance(nome, cognome, indirizzo, telefono, dataNascita);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.company_fragment, fragment, "BLANK_FRAGMENT").commit();

    }


    /////////////////////
    private void secondRegistrationPart() {

        final String nome = txtNome.getText().toString().trim();
        final String cognome = txtCognome.getText().toString().trim();
        final String indirizzo = txtIndirizzo.getText().toString().trim();
        final String telefono = txtTelefono.getText().toString().trim();
        final String dataNascita = txtDataNascita.getText().toString().trim();
        final String sesso = "m";


        //first we will do the validations

        if (TextUtils.isEmpty(nome)) {
            txtNome.setError("Please enter nome");
            txtNome.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(cognome)) {
            txtCognome.setError("Please enter your cognome");
            txtCognome.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(indirizzo)) {
            txtIndirizzo.setError("Please enter your indirizzo");
            txtIndirizzo.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(telefono)) {
            txtTelefono.setError("Please enter your telefono");
            txtTelefono.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(dataNascita)) {
            txtDataNascita.setError("Please enter your dataNascita");
            txtDataNascita.requestFocus();
            return;
        }


        openFragment2(nome, cognome, indirizzo, telefono, dataNascita);


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

    @Override
    public void onFragmentInteraction(Uri uri) {

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

package it.uniba.di.sms.carpooling.Accesso;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import it.uniba.di.sms.carpooling.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegistrationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";


    private EditText txtNome;
    private EditText txtCognome;
    private EditText txtCitta;
    private TextView txtDataNascita;
    private EditText txtIndirizzo;
    private EditText txtTelefono;
    private Button btnAvanti;

    //Inizializzazione variabili Inserimento data
    private Date time;
    int year, month, day;
    Calendar calendar = new GregorianCalendar();
    DatePickerDialog.OnDateSetListener mDateSetListener = null;
    Calendar cal = Calendar.getInstance();


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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_avanti_registration_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.avanti_rf:
                secondRegistrationPart();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        Toolbar toolbar = view.findViewById(R.id.my_toolbar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        setHasOptionsMenu(true);

        txtNome = view.findViewById(R.id.nomeText);
        txtCognome = view.findViewById(R.id.cognomeText);
        txtCitta=view.findViewById(R.id.cittaText);
        txtIndirizzo = view.findViewById(R.id.indirizzoText);
        txtTelefono = view.findViewById(R.id.telefonoText);
        txtDataNascita = view.findViewById(R.id.dataNascitaText);
        btnAvanti = view.findViewById(R.id.btnAvanti);

        btnAvanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondRegistrationPart();
            }
        });

        //Inserimento data
        txtDataNascita = view.findViewById(R.id.dataNascitaText);

        //Set current date
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        txtDataNascita.setOnClickListener(new View.OnClickListener() {
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
                txtDataNascita.setText(date);

            }
        };


        //fragment
        companyFragment = view.findViewById(R.id.company_fragment);


        // Inflate the layout for this fragment
        return view;
    }


    public void openFragment(String username, String password, String email, String nome, String cognome, String indirizzo,
                             String telefono, String dataNascita) {
        CompanyFragment companyFrag=CompanyFragment.newInstance(username, password,email,nome,cognome,indirizzo,telefono,dataNascita);
        FragmentManager manager=getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.add(R.id.company_fragment, companyFrag, companyFrag.getTag()).commit();
    }


    private void secondRegistrationPart() {

        final String nome = txtNome.getText().toString().trim();
        final String cognome = txtCognome.getText().toString().trim();
        final String citta = txtCitta.getText().toString().trim();
        String indirizzo = txtIndirizzo.getText().toString().trim();
        final String telefono = txtTelefono.getText().toString().trim();
        final String dataNascita = txtDataNascita.getText().toString().trim();


        //first we will do the validations

        if (TextUtils.isEmpty(nome)) {
            txtNome.setError("Campo nome non compilato");
            txtNome.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(cognome)) {
            txtCognome.setError("Campo cognome non compilato");
            txtCognome.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(citta)) {
            txtCitta.setError("Campo città non compilato");
            txtCitta.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(indirizzo)) {
            txtIndirizzo.setError("Campo indirizzo non compilato");
            txtIndirizzo.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(telefono)) {
            txtTelefono.setError("Campo telefono non compilato");
            txtTelefono.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(dataNascita)) {
            txtDataNascita.setError("Campo data nascita non compilato");
            txtDataNascita.requestFocus();
            return;
        }

        indirizzo= indirizzo.concat(" " + citta);
        openFragment(usernameParam,passwordParam,emailParam,nome,cognome,indirizzo,telefono,dataNascita);

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

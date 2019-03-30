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
import android.text.InputType;
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
import android.widget.TextView;
import java.util.Calendar;
import java.util.Date;
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

    private EditText txtName;
    private EditText txtSurname;
    private EditText txtCity;
    private TextView txtBirthDate;
    private EditText txtAddress;
    private EditText txtTelephone;

    //Inizialization of the Date variable
    private Date time;
    int year, month, day;
    DatePickerDialog.OnDateSetListener mDateSetListener = null;
    Calendar cal = Calendar.getInstance();

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
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        Toolbar toolbar = view.findViewById(R.id.my_toolbar);
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

        txtName = view.findViewById(R.id.textName);
        txtName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        txtSurname = view.findViewById(R.id.textSurname);
        txtSurname.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        txtCity =view.findViewById(R.id.textCity);
        txtCity.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        txtAddress = view.findViewById(R.id.textAddress);
        txtAddress.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        txtTelephone = view.findViewById(R.id.textTelephone);
        txtBirthDate = view.findViewById(R.id.textBirthDate);
        Button forwardButton = view.findViewById(R.id.thirdRegPart);

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondRegistrationPart();
            }
        });

        //Insert date
        txtBirthDate = view.findViewById(R.id.textBirthDate);

        //Set current date
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        txtBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Dialog, mDateSetListener, year,month,day);
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
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
                txtBirthDate.setText(date);

            }
        };

        // Inflate the layout for this fragment
        return view;
    }


    public void openFragment(String username, String password, String email, String name, String surname, String address,
                             String telephone, String birthDate) {
        CompanyFragment companyFrag=CompanyFragment.newInstance(username, password,email,name,surname,address,telephone,birthDate);
        FragmentManager manager=getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.add(R.id.company_fragment, companyFrag, "BLANK_FRAGMENT").commit();
    }


    private void secondRegistrationPart() {

        final String name = txtName.getText().toString().trim();
        final String surname = txtSurname.getText().toString().trim();
        final String city = txtCity.getText().toString().trim();
        String address = txtAddress.getText().toString().trim();
        final String telephone = txtTelephone.getText().toString().trim();
        final String birthDate = txtBirthDate.getText().toString().trim();

        //first we will do the validations

        if (TextUtils.isEmpty(name)) {
            txtName.setError(getText(R.string.enter_name));
            txtName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(surname)) {
            txtSurname.setError(getText(R.string.enter_surname));
            txtSurname.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(city)) {
            txtCity.setError(getText(R.string.enter_city));
            txtCity.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            txtAddress.setError(getText(R.string.enter_address));
            txtAddress.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(telephone)) {
            txtTelephone.setError(getText(R.string.enter_telephone));
            txtTelephone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(birthDate)) {
            txtBirthDate.setError(getText(R.string.enter_birthdate));
            txtBirthDate.requestFocus();
            return;
        }

        address= address.concat(" " + city);
        openFragment(usernameParam,passwordParam,emailParam,name,surname,address,telephone,birthDate);

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

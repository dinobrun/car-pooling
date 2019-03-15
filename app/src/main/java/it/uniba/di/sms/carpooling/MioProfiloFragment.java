package it.uniba.di.sms.carpooling;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import it.uniba.di.sms.carpooling.Automobile.Automobile;


public class MioProfiloFragment extends Fragment {

    EditText txtNome, txtCognome, txtDataNascita, txtIndirizzo, txtEmail, txtTelefono;


    public MioProfiloFragment() {

    }


    public static MioProfiloFragment newInstance(String param1, String param2) {
        MioProfiloFragment fragment = new MioProfiloFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.edit_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_icon:
                //txtNome.setEnabled(true);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_mio_profilo, container, false);

        txtNome = v.findViewById(R.id.nome);
        txtNome.setText(SharedPrefManager.getInstance(getActivity()).getUser().getNome());

        txtCognome = v.findViewById(R.id.cognome);
        txtCognome.setText(SharedPrefManager.getInstance(getActivity()).getUser().getCognome());

        txtDataNascita = v.findViewById(R.id.data_nascita);
        txtDataNascita.setText(SharedPrefManager.getInstance(getActivity()).getUser().getDataNascita());

        txtIndirizzo = v.findViewById(R.id.indirizzo);
        txtIndirizzo.setText(SharedPrefManager.getInstance(getActivity()).getUser().getIndirizzo());

        txtEmail = v.findViewById(R.id.email);
        txtEmail.setText(SharedPrefManager.getInstance(getActivity()).getUser().getEmail());

        txtTelefono = v.findViewById(R.id.telefono);
        txtTelefono.setText(SharedPrefManager.getInstance(getActivity()).getUser().getTelefono());



        String username = SharedPrefManager.getInstance(getActivity()).getUser().getUsername();

        Toolbar toolbar = v.findViewById(R.id.my_toolbar);
        toolbar.setTitle(username);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        setHasOptionsMenu(true);




        // Inflate the layout for this fragment
        return v;
    }


}

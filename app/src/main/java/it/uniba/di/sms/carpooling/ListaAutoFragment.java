package it.uniba.di.sms.carpooling;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


public class ListaAutoFragment extends Fragment {

    String autoNameInput;
    int numPostiInput;

    public ListaAutoFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ListaAutoFragment newInstance() {
        ListaAutoFragment fragment = new ListaAutoFragment();
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
        inflater.inflate(R.menu.add_auto_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_auto:
                showAddAutoPopup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_lista_auto, container, false);

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
        // Inflate the layout for this fragment
        return v;
    }

    public void showAddAutoPopup(){

        final ArrayList<String> automobili = new ArrayList<>();

        AlertDialog.Builder builderNomeAuto = new AlertDialog.Builder(getActivity());
        builderNomeAuto.setTitle("Aggiungi una auto");
        builderNomeAuto.setMessage("Inserisci il nome dell'auto da aggiungere");

        // Set up the input
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        builderNomeAuto.setView(input);

        // Set up the buttons
        builderNomeAuto.setPositiveButton("Avanti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                autoNameInput = input.getText().toString();

                AlertDialog.Builder builderPostiAuto = new AlertDialog.Builder(getActivity());
                builderPostiAuto.setTitle("Numero di posti");
                builderPostiAuto.setMessage("Inserisci il numero di posti dell'auto disponibili");

                // Set up the input
                final EditText input = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builderPostiAuto.setView(input);
                builderPostiAuto.setPositiveButton("Avanti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        numPostiInput = Integer.parseInt(input.getText().toString());
                        Toast.makeText(getActivity(),autoNameInput+" "+Integer.toString(numPostiInput),Toast.LENGTH_SHORT).show();
                        /*autoName = input.getText().toString();
                        addAuto();
                        automobili.add(autoName);
                        openCreaPassaggioFragment(automobili);*/
                    }
                });

                builderPostiAuto.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builderPostiAuto.show();

            }
        });

        builderNomeAuto.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builderNomeAuto.show();

    }


}

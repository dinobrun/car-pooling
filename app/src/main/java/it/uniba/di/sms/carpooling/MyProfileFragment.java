package it.uniba.di.sms.carpooling;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;


public class MyProfileFragment extends Fragment {

    EditText txtNome, txtCognome, txtDataNascita, txtIndirizzo, txtEmail, txtTelefono;
    ImageView profileImage;


    public MyProfileFragment() {

    }


    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);

        txtNome = v.findViewById(R.id.nome);
        txtNome.setText(SharedPrefManager.getInstance(getActivity()).getUser().getNome() + " " +
                SharedPrefManager.getInstance(getActivity()).getUser().getCognome());
        txtDataNascita = v.findViewById(R.id.data_nascita);
        txtDataNascita.setText(SharedPrefManager.getInstance(getActivity()).getUser().getDataNascita());

        txtIndirizzo = v.findViewById(R.id.indirizzo);
        txtIndirizzo.setText(SharedPrefManager.getInstance(getActivity()).getUser().getIndirizzo());

        txtEmail = v.findViewById(R.id.email);
        txtEmail.setText(SharedPrefManager.getInstance(getActivity()).getUser().getEmail());

        txtTelefono = v.findViewById(R.id.telefono);
        txtTelefono.setText(SharedPrefManager.getInstance(getActivity()).getUser().getTelefono());

        profileImage = v.findViewById(R.id.imageView);
        if(SharedPrefManager.getInstance(getActivity()).getUser().getFoto().equals("null")){
            profileImage.setBackgroundResource(R.drawable.no_profile);
        }
        else{
            byte[] decodedString = Base64.decode(SharedPrefManager.getInstance(getActivity()).getUser().getFoto(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profileImage.setImageBitmap(decodedByte);
        }



        String username = SharedPrefManager.getInstance(getActivity()).getUser().getUsername();

        Toolbar toolbar = v.findViewById(R.id.my_toolbar);
        toolbar.setTitle(username);
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




        // Inflate the layout for this fragment
        return v;
    }


}

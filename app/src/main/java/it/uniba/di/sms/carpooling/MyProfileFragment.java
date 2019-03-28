package it.uniba.di.sms.carpooling;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
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
import android.widget.Toast;


public class MyProfileFragment extends Fragment {

    EditText txtName, txtSurname, txtBirthDate, txtAddress, txtEmail, txtTelephone, txtCompany;
    ImageView profileImage;


    public MyProfileFragment() {

    }


    public static MyProfileFragment newInstance() {
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

        txtName = v.findViewById(R.id.nameMyProfile);
        txtName.setText(SharedPrefManager.getInstance(getActivity()).getUser().getNome()
                        .concat(" "+SharedPrefManager.getInstance(getActivity()).getUser().getCognome()));
        txtBirthDate = v.findViewById(R.id.birthDateMyProfile);
        txtBirthDate.setText(SharedPrefManager.getInstance(getActivity()).getUser().getDataNascita());

        txtAddress = v.findViewById(R.id.addressMyProfile);
        txtAddress.setText(SharedPrefManager.getInstance(getActivity()).getUser().getIndirizzo());

        txtEmail = v.findViewById(R.id.emailMyProfile);
        txtEmail.setText(SharedPrefManager.getInstance(getActivity()).getUser().getEmail());

        txtTelephone = v.findViewById(R.id.telephoneMyProfile);
        txtTelephone.setText(SharedPrefManager.getInstance(getActivity()).getUser().getTelefono());

        txtCompany = v.findViewById(R.id.companyMyProfile);
        txtCompany.setText(SharedPrefManager.getInstance(getActivity()).getUser().getAzienda());

        profileImage = v.findViewById(R.id.imageMyProfile);
        if(SharedPrefManager.getInstance(getActivity()).getUser().getFoto().equals("null")){
            profileImage.setBackgroundResource(R.drawable.no_profile);
        }
        else{
            byte[] decodedString = Base64.decode(SharedPrefManager.getInstance(getActivity()).getUser().getFoto(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Bitmap imageMyProfile=roundCorner(decodedByte, 400);
            profileImage.setImageBitmap(imageMyProfile);
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

    public static Bitmap roundCorner(Bitmap src, float round)
    {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();

        // create bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // set canvas for painting
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        // config paint
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        // config rectangle for embedding
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        // draw rect to canvas
        canvas.drawRoundRect(rectF, round, round, paint);

        // create Xfer mode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // draw source image to canvas
        canvas.drawBitmap(src, rect, rect, paint);

        // return final image
        return result;
    }


}

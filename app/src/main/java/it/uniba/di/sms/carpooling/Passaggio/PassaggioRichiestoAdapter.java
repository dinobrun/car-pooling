package it.uniba.di.sms.carpooling.Passaggio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.uniba.di.sms.carpooling.R;


public class PassaggioRichiestoAdapter extends RecyclerView.Adapter<PassaggioRichiestoAdapter.ProductViewHolder> implements RecyclerView.OnItemTouchListener {


    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Passaggio> passaggioList;

    //getting the context and product list with constructor
    public PassaggioRichiestoAdapter(Context mCtx, List<Passaggio> passaggioList) {
        this.mCtx = mCtx;
        this.passaggioList = passaggioList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_passaggio_richiesto, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //getting the product of the specified position
        Passaggio passaggio = passaggioList.get(position);

        if(passaggio.getFoto() != null){
            byte[] decodedString = Base64.decode(passaggio.getFoto(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.profileImage.setImageBitmap(decodedByte);
        }else{
            holder.profileImage.setBackgroundResource(R.drawable.no_profile);
        }

        holder.textViewTitle.setText(passaggio.getAutista());
        holder.textViewShortDesc.setText(passaggio.getAutomobile());
        holder.textViewRating.setText(passaggio.getDirezione());
        holder.textViewPrice.setText(passaggio.getData());
    }

    @Override
    public int getItemCount() {
        return passaggioList.size();
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
        ImageView profileImage;

        public ProductViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            profileImage = itemView.findViewById(R.id.tripImage);

        }
    }
}
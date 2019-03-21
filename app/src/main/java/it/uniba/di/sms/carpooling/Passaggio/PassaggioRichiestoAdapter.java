package it.uniba.di.sms.carpooling.Passaggio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.sms.carpooling.R;


public class PassaggioRichiestoAdapter extends RecyclerView.Adapter<PassaggioRichiestoAdapter.ProductViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Passaggio> passaggioList;
    private List<Integer> selectedIds = new ArrayList<>();

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

        int id = passaggioList.get(position).getId();

        if (selectedIds.contains(id)){
            //if item is selected then,set foreground color of FrameLayout.
            holder.itemView.setBackgroundColor(Color.GRAY);
        }
        else {
            //else remove selected item color.
            holder.itemView.setForeground(new ColorDrawable(ContextCompat.getColor(mCtx,android.R.color.transparent)));
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        if(passaggio.getFoto() != null){
            byte[] decodedString = Base64.decode(passaggio.getFoto(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.profileImage.setImageBitmap(decodedByte);
        }else{
            holder.profileImage.setBackgroundResource(R.drawable.no_profile);
        }

        holder.textViewTitle.setText(passaggio.getNomeAutista() + " " + passaggio.getCognomeAutista());
        holder.textViewShortDesc.setText(passaggio.getAutomobile());

        if(passaggio.getConcluso()==1)
            holder.textViewRating.setText(R.string.concluded);
        else
            holder.textViewRating.setText(R.string.ongoing);

        holder.textViewPrice.setText(passaggio.getData());
    }

    @Override
    public int getItemCount() {
        return passaggioList.size();
    }

    public Passaggio getItem(int position){
        return passaggioList.get(position);
    }

    public void setSelectedIds(List<Integer> selectedIds) {
        this.selectedIds = selectedIds;
        notifyDataSetChanged();
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
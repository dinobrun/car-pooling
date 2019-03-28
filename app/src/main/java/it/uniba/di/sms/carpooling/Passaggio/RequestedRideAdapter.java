package it.uniba.di.sms.carpooling.Passaggio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.uniba.di.sms.carpooling.R;


public class RequestedRideAdapter extends RecyclerView.Adapter<RequestedRideAdapter.ProductViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Passaggio> passaggioList;
    private List<Integer> selectedIds = new ArrayList<>();

    //getting the context and product list with constructor
    public RequestedRideAdapter(Context mCtx, List<Passaggio> passaggioList) {
        this.mCtx = mCtx;
        this.passaggioList = passaggioList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_requested_ride, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //getting the product of the specified position
        Passaggio passaggio = passaggioList.get(position);

        int id = passaggioList.get(position).getId();

        if (selectedIds.contains(id)){
            //if item is selected then,set foreground color of FrameLayout.
            holder.overlayLayout.setVisibility(View.VISIBLE);
        }
        else {
            //else remove selected item color.
            holder.overlayLayout.setVisibility(View.INVISIBLE);
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

        switch (passaggio.getConfermato()){
            case 0:
                holder.textViewRating.setText(R.string.wait_conferm);
                holder.textViewRating.setTextColor(Color.rgb(255,165,0));
                break;
            case 1:
                holder.textViewRating.setText(R.string.confermed);
                holder.textViewRating.setTextColor(Color.rgb(139,195,74));
                break;
            case 2:
                holder.textViewRating.setText(R.string.rejected);
                holder.textViewRating.setTextColor(Color.rgb(255,0,0));
                break;
            default:
                holder.textViewRating.setText(R.string.wait_conferm);
        }

        if(passaggio.getConcluso() != 0){
            holder.textViewConcluded.setVisibility(View.VISIBLE);
            holder.textViewConcluded.setTextColor(Color.rgb(255,0,0));
        }


        // First convert the String to a Date
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ITALIAN);
        Date date = null;
        try {
            date = dateParser.parse(passaggio.getData());
            // Then convert the Date to a String, formatted as you dd/MM/yyyy
            SimpleDateFormat dateFormatter = new SimpleDateFormat("E d MMM yyyy HH:mm", Locale.ITALY);
            holder.textViewPrice.setText(dateFormatter.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

        TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice, textViewConcluded;
        ImageView profileImage;
        RelativeLayout overlayLayout;

        public ProductViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewUsername);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            profileImage = itemView.findViewById(R.id.tripImage);
            textViewConcluded = itemView.findViewById(R.id.lblConcluded);
            overlayLayout = itemView.findViewById(R.id.overlayLayout);
        }
    }
}
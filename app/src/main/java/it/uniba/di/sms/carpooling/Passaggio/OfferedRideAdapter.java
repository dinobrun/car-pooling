package it.uniba.di.sms.carpooling.Passaggio;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
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

public class OfferedRideAdapter extends RecyclerView.Adapter<OfferedRideAdapter.ProductViewHolder>  {
    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Passaggio> passaggioList;
    private List<Integer> selectedIds = new ArrayList<>();


    //getting the context and product list with constructor
    public OfferedRideAdapter(Context mCtx, List<Passaggio> passaggioList) {
        this.mCtx = mCtx;
        this.passaggioList = passaggioList;
    }

    @Override
    public OfferedRideAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_offered_ride, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new OfferedRideAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OfferedRideAdapter.ProductViewHolder holder, int position) {
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


        if(passaggio.getDirezione()==0){
            holder.profileImage.setImageResource(R.drawable.one_way_icon);
            holder.lblDirection.setText(R.string.one_way);
            holder.lblDirection.setTextColor(Color.rgb(16,163,23));
        }
        else if(passaggio.getDirezione()==1){
            holder.profileImage.setImageResource(R.drawable.return_icon);
            holder.lblDirection.setText(R.string.backHome);
            holder.lblDirection.setTextColor(Color.rgb(204,0,0));
        }

        holder.textViewShortDesc.setText(passaggio.getAutomobile());

        //if there are pending requests
        if(passaggio.getRichiesteInSospeso() > 0){
            holder.textViewRating.setTextColor(Color.rgb(255,0,0));
        }
        holder.textViewRating.setText(Integer.toString(passaggio.getRichiesteInSospeso()));

        if(passaggio.getConcluso() != 0){
            holder.textViewConcluded.setTextColor(Color.rgb(255,0,0));
            holder.textViewConcluded.setVisibility(View.VISIBLE);
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

        TextView lblDirection, textViewShortDesc, textViewRating, textViewPrice, textViewConcluded;;
        ImageView profileImage;
        RelativeLayout overlayLayout;

        public ProductViewHolder(View itemView) {
            super(itemView);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            profileImage = itemView.findViewById(R.id.tripImage);
            lblDirection = itemView.findViewById(R.id.lblDirection);
            textViewConcluded = itemView.findViewById(R.id.lblConcluded);
            overlayLayout = itemView.findViewById(R.id.overlayLayout);
        }

    }




}

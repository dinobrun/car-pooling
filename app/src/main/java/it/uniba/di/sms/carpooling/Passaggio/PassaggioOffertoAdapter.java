package it.uniba.di.sms.carpooling.Passaggio;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.sms.carpooling.R;

public class PassaggioOffertoAdapter extends RecyclerView.Adapter<PassaggioOffertoAdapter.ProductViewHolder>  {
    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Passaggio> passaggioList;
    private List<Integer> selectedIds = new ArrayList<>();


    //getting the context and product list with constructor
    public PassaggioOffertoAdapter(Context mCtx, List<Passaggio> passaggioList) {
        this.mCtx = mCtx;
        this.passaggioList = passaggioList;
    }

    @Override
    public PassaggioOffertoAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_passaggio_offerto, null);
        return new PassaggioOffertoAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PassaggioOffertoAdapter.ProductViewHolder holder, int position) {
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


        if(passaggio.getDirezione()==0){
            holder.profileImage.setImageResource(R.drawable.andata_icon);
        }
        else if(passaggio.getDirezione()==1){
            holder.profileImage.setImageResource(R.drawable.ritorno_icon);
        }

        holder.textViewTitle.setText(passaggio.getUsernameAutista());
        holder.textViewShortDesc.setText(passaggio.getAutomobile());
        holder.textViewRating.setText(Integer.toString(passaggio.getRichiesteInSospeso()));
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

package it.uniba.di.sms.carpooling;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class PassaggioAdapter extends RecyclerView.Adapter<PassaggioAdapter.ProductViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Passaggio> passaggioList;

    //getting the context and product list with constructor
    public PassaggioAdapter(Context mCtx, List<Passaggio> passaggioList) {
        this.mCtx = mCtx;
        this.passaggioList = passaggioList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //getting the product of the specified position
        Passaggio passaggio = passaggioList.get(position);

        //binding the data with the viewholder views
        holder.textViewTitle.setText(passaggio.getAutista());
        holder.textViewShortDesc.setText(passaggio.getAutomobile());
        holder.textViewRating.setText(passaggio.getDirezione());



    }


    @Override
    public int getItemCount() {
        return passaggioList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
        ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
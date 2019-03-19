package it.uniba.di.sms.carpooling.Passaggio;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.Utente;

public class PassengersAdapter extends RecyclerView.Adapter<PassengersAdapter.ProductViewHolder> implements RecyclerView.OnItemTouchListener {


    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Utente> passeggeriList;

    //getting the context and product list with constructor
    public PassengersAdapter(Context mCtx, List<Utente> passeggeriParam) {
        this.mCtx = mCtx;
        this.passeggeriList = passeggeriParam;
    }

    @Override
    public PassengersAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout_passeggero, null);
        return new PassengersAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PassengersAdapter.ProductViewHolder holder, int position) {
        //getting the product of the specified position
        Utente utente = passeggeriList.get(position);

        //binding the data with the viewholder views
        holder.textViewUsername.setText(utente.getUsername());
        holder.textViewIndirizzo.setText((utente.getIndirizzo()));

    }



    @Override
    public int getItemCount() {
        return passeggeriList.size();
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

        TextView textViewUsername, textViewIndirizzo;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewUsername = itemView.findViewById(R.id.textViewTitle);
            textViewIndirizzo = itemView.findViewById(R.id.textViewShortDesc);
        }
    }
}
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
        View view = inflater.inflate(R.layout.list_passengers, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new PassengersAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PassengersAdapter.ProductViewHolder holder, int position) {
        //getting the product of the specified position
        Utente utente = passeggeriList.get(position);

        //binding the data with the viewholder views
        holder.textViewNome.setText(utente.getNome().concat(" " + utente.getCognome()));
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

        TextView textViewNome, textViewCognome;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewNome = itemView.findViewById(R.id.textViewNome);
        }
    }
}
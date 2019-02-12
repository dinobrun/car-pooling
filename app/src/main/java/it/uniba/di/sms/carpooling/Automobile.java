package it.uniba.di.sms.carpooling;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Automobile {

    private int numPosti;
    private String nome, idUtente;

    public Automobile(String nome, int numPosti, String idUtente) {
        this.numPosti = numPosti;
        this.nome = nome;
        this.idUtente = idUtente;
    }

    public Automobile() {
    }

    public int getNumPosti() {
        return numPosti;
    }

    public void setNumPosti(int numPosti) {
        this.numPosti = numPosti;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

}

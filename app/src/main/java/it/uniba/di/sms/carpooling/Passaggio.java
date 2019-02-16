package it.uniba.di.sms.carpooling;

import java.io.Serializable;

public class Passaggio implements Serializable {

    private String autista, data, automobile, azienda, direzione, indirizzo;
    private int id, numPosti;

    public Passaggio(int id, String autista,String indirizzo, String data, String automobile, String azienda, String direzione, int numPosti) {
        this.id=id;
        this.autista = autista;
        this.data = data;
        this.automobile = automobile;
        this.azienda = azienda;
        this.direzione = direzione;
        this.numPosti = numPosti;
        this.indirizzo=indirizzo;
    }

    public String getIndirizzo(){
        return indirizzo;
    }

    public int getId(){
        return id;
    }

    public String getAutista() {
        return autista;
    }

    public void setAutista(String autista) {
        this.autista = autista;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAutomobile() {
        return automobile;
    }

    public void setAutomobile(String automobile) {
        this.automobile = automobile;
    }

    public String getAzienda() {
        return azienda;
    }

    public void setAzienda(String azienda) {
        this.azienda = azienda;
    }

    public String getDirezione() {
        return direzione;
    }

    public void setDirezione(String direzione) {
        this.direzione = direzione;
    }

    public int getNumPosti() {
        return numPosti;
    }

    public void setNumPosti(int numPosti) {
        this.numPosti = numPosti;
    }
}

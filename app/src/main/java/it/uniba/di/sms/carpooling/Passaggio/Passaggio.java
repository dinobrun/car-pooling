package it.uniba.di.sms.carpooling.Passaggio;

import java.io.Serializable;

import it.uniba.di.sms.carpooling.Utente;

public class Passaggio implements Serializable {

    private String autista, data, automobile, azienda, direzione, indirizzo, foto;
    private Utente utente;
    private int id, numPosti;
    private boolean richiesto = false;
    private boolean confermato = false;

    public boolean isRichiesto() {
        return richiesto;
    }

    public void setRichiesto(boolean richiesto) {
        this.richiesto = richiesto;
    }

    public boolean isConfermato() {
        return confermato;
    }

    public void setConfermato(boolean confermato) {
        this.confermato = confermato;
    }

    public Passaggio(int id, String autista, String indirizzo, String data, String automobile, String azienda, String direzione, int numPosti) {
        this.id=id;
        this.autista = autista;
        this.data = data;
        this.automobile = automobile;
        this.azienda = azienda;
        this.direzione = direzione;
        this.numPosti = numPosti;
        this.indirizzo=indirizzo;
    }

    public Passaggio(int id, Utente utente, String data, String automobile, String azienda, String direzione, int numPosti) {
        this.id=id;
        this.utente = utente;
        this.data = data;
        this.automobile = automobile;
        this.azienda = azienda;
        this.direzione = direzione;
        this.numPosti = numPosti;
    }

    public String getFoto() {
        return foto;
    }

    public Passaggio(int id, String autista, String indirizzo, String data, String automobile, String azienda, String direzione, int numPosti, boolean confermato, String foto) {
        this.id=id;
        this.autista = autista;
        this.data = data;
        this.automobile = automobile;
        this.azienda = azienda;
        this.direzione = direzione;
        this.numPosti = numPosti;
        this.indirizzo=indirizzo;
        this.confermato=confermato;
        this.foto = foto;
    }

    public Passaggio(int id, String autista, String indirizzo, String data, String automobile, String azienda, String direzione, int numPosti, boolean confermato) {
        this.id=id;
        this.autista = autista;
        this.data = data;
        this.automobile = automobile;
        this.azienda = azienda;
        this.direzione = direzione;
        this.numPosti = numPosti;
        this.indirizzo=indirizzo;
        this.confermato=confermato;
    }

    public boolean getConfermato(){
        return confermato;
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



    //costruttore temporaneo
    public Passaggio(String autista, String automobile, String direzione) {
        this.autista = autista;
        this.automobile = automobile;
        this.direzione = direzione;
    }

    public Utente getUtente() {
        return utente;
    }


}

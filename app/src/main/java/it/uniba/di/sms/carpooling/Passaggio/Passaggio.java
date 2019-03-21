package it.uniba.di.sms.carpooling.Passaggio;

import java.io.Serializable;

import it.uniba.di.sms.carpooling.Utente;

public class Passaggio implements Serializable {

    private String usernameAutista, data, automobile, azienda, indirizzo, foto, nomeAutista, cognomeAutista, telefonoAutista;
    private Utente utente;
    private int id, numPosti, direzione;

    private boolean concluso, iniziato;

    public boolean isIniziato() {
        return iniziato;
    }

    public void setIniziato(boolean iniziato) {
        this.iniziato = iniziato;
    }

    public String getNomeAutista() {
        return nomeAutista;
    }

    public String getCognomeAutista() {
        return cognomeAutista;
    }

    public String getTelefonoAutista() {
        return telefonoAutista;
    }

    private boolean richiesto = false;
    private int confermato = 0;

    public int getRichiesteInSospeso() {
        return richiesteInSospeso;
    }

    public void setRichiesteInSospeso(int richiesteInSospeso) {
        this.richiesteInSospeso = richiesteInSospeso;
    }

    private int richiesteInSospeso = 0;

    public boolean isRichiesto() {
        return richiesto;
    }

    public void setRichiesto(boolean richiesto) {
        this.richiesto = richiesto;
    }

    public int getConfermato() {
        return confermato;
    }

    public void setConfermato(int confermato) {
        this.confermato = confermato;
    }

    public String getFoto() {
        return foto;
    }

    //costruttore PassaggiOffertiFragment
    public Passaggio(int id, String usernameAutista, String indirizzo, String data, String automobile, String azienda, int direzione, int numPosti, boolean concluso) {
        this.id=id;
        this.usernameAutista = usernameAutista;
        this.data = data;
        this.automobile = automobile;
        this.azienda = azienda;
        this.direzione = direzione;
        this.numPosti = numPosti;
        this.indirizzo=indirizzo;
        this.concluso= concluso;
    }



    //costruttore PassaggiRichiestiFragment
    public Passaggio(int id, String indirizzo, String nomeAutista, String cognomeAutista, String telefonoAutista, String data, String automobile, String azienda, int direzione, int numPosti, int confermato, String foto, boolean concluso, boolean iniziato) {
        this.id=id;
        this.indirizzo = indirizzo;
        this.nomeAutista = nomeAutista;
        this.cognomeAutista = cognomeAutista;
        this.telefonoAutista = telefonoAutista;
        this.data = data;
        this.automobile = automobile;
        this.azienda = azienda;
        this.direzione = direzione;
        this.numPosti = numPosti;
        this.confermato=confermato;
        this.foto = foto;
        this.concluso=concluso;
        this.iniziato = iniziato;
    }

    //costruttore FindRideFragment
    public Passaggio(int id, String indirizzo, String usernameAutista, String nomeAutista, String cognomeAutista, String telefonoAutista, String data, String automobile, int numPosti, String azienda, int direzione, String foto) {
        this.id=id;
        this.indirizzo = indirizzo;
        this.usernameAutista = usernameAutista;
        this.nomeAutista = nomeAutista;
        this.cognomeAutista = cognomeAutista;
        this.telefonoAutista = telefonoAutista;
        this.data = data;
        this.automobile = automobile;
        this.azienda = azienda;
        this.direzione = direzione;
        this.numPosti = numPosti;
        this.foto = foto;
    }

    public Passaggio(int id, String usernameAutista, String indirizzo, String data, String automobile, String azienda, int direzione, int numPosti, int confermato) {
        this.id=id;
        this.usernameAutista = usernameAutista;
        this.data = data;
        this.automobile = automobile;
        this.azienda = azienda;
        this.direzione = direzione;
        this.numPosti = numPosti;
        this.indirizzo=indirizzo;
        this.confermato=confermato;
    }

    //costruttore temporaneo
    public Passaggio(String usernameAutista, String automobile, int direzione) {
        this.usernameAutista = usernameAutista;
        this.automobile = automobile;
        this.direzione = direzione;
    }


    public String getIndirizzo(){
        return indirizzo;
    }

    public int getId(){
        return id;
    }

    public String getUsernameAutista() {
        return usernameAutista;
    }

    public void setUsernameAutista(String usernameAutista) {
        this.usernameAutista = usernameAutista;
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

    public int getDirezione() {
        return direzione;
    }

    public void setDirezione(int direzione) {
        this.direzione = direzione;
    }

    public int getNumPosti() {
        return numPosti;
    }

    public void setNumPosti(int numPosti) {
        this.numPosti = numPosti;
    }

    public Utente getUtente() {
        return utente;
    }

    public boolean isConcluso() {
        return concluso;
    }


}

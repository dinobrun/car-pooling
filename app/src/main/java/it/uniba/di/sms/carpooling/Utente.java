package it.uniba.di.sms.carpooling;

import java.io.Serializable;

public class Utente implements Serializable {
    private String username, nome, cognome, sesso, dataNascita, indirizzo, email, telefono, indirizzoAzienda;
    private String foto = null;
    private String azienda = null;
    private int confermato;

    private int autorizzato;

    public String getIndirizzoAzienda() {
        return indirizzoAzienda;
    }

    public String getUsername() {
        return username;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDataNascita() {
        return dataNascita;
    }

    public String getAzienda(){
        return azienda;
    }

    public void setConfermato(int confermato) {
        this.confermato = confermato;
    }

    public int getConfermato() {
        return confermato;
    }

    public void setAutorizzato(int confermato) {
        this.autorizzato = confermato;
    }

    public int getAutorizzato() {
        return autorizzato;
    }


    //costruttore di utente nel login senza azienda
    public Utente(String username, String nome, String cognome, String indirizzo, String email, String telefono, String dataNascita, int autorizzato) {
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.indirizzo = indirizzo;
        this.email = email;
        this.telefono = telefono;
        this.dataNascita = dataNascita;
        this.autorizzato = autorizzato;
    }

    //costruttore utilizzato per il tracking di un passaggio in TrackingService
    public Utente(String nome, String cognome){
        this.nome = nome;
        this.cognome = cognome;
    }

    //costruttore di utente nel login con l'azienda
    public Utente(String username, String nome, String cognome, String indirizzo, String email, String telefono, String dataNascita, String azienda, String indirizzoAzienda, int autorizzato) {
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.indirizzo = indirizzo;
        this.email = email;
        this.telefono = telefono;
        this.dataNascita = dataNascita;
        this.azienda=azienda;
        this.indirizzoAzienda = indirizzoAzienda;
        this.autorizzato = autorizzato;
    }


    //costruttore di utenti per la creazione dei marker
    public Utente(String username, String nome, String cognome, String indirizzo, String email, String telefono, int confermato) {
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.indirizzo = indirizzo;
        this.email = email;
        this.telefono = telefono;
        this.confermato = confermato;
    }





    public void addAzienda(String azienda){
        this.azienda=azienda;
    }

    public String getFoto() {
        return foto;
    }
}

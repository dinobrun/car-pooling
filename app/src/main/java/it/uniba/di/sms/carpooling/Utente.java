package it.uniba.di.sms.carpooling;

import java.util.ArrayList;
import java.util.Date;

public class Utente {
    private String username, nome, cognome, sesso, dataNascita, indirizzo, email, telefono;
    private String azienda = null;

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


    public Utente(String username, String nome, String cognome, String indirizzo, String email, String telefono, String dataNascita) {
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.indirizzo = indirizzo;
        this.email = email;
        this.telefono = telefono;
        this.dataNascita = dataNascita;
    }

    public Utente(String username, String nome, String cognome, String indirizzo, String email, String telefono, String dataNascita, String azienda) {
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.indirizzo = indirizzo;
        this.email = email;
        this.telefono = telefono;
        this.dataNascita = dataNascita;
        this.azienda=azienda;
    }


    public Utente(String nome, String cognome) {
        this.nome = nome;
        this.cognome = cognome;
    }

    public void addAzienda(String azienda){
        this.azienda=azienda;
    }

}

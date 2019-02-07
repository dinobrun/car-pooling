package it.uniba.di.sms.carpooling;

import java.util.ArrayList;
import java.util.Date;

public class Utente {
    private String username, nome, cognome, sesso, dataNascita, indirizzo, email, telefono;

    public Utente(String nome, String cognome) {
        this.nome = nome;
        this.cognome = cognome;
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

    public String getSesso() {
        return sesso;
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

    public Utente(String username, String nome, String cognome, String sesso, String indirizzo, String email, String telefono, String dataNascita) {
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.sesso = sesso;
        this.indirizzo = indirizzo;
        this.email = email;
        this.telefono = telefono;
        this.dataNascita = dataNascita;
    }

}

package it.uniba.di.sms.carpooling;

public class Utente {
    private String username, nome, cognome, sesso, dataNascita, indirizzo, email, telefono, indirizzoAzienda, foto;
    private String azienda = null;
    private int confermato;

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

    public Utente(String username, String nome, String cognome, String indirizzo, String email, String telefono, String dataNascita) {
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.indirizzo = indirizzo;
        this.email = email;
        this.telefono = telefono;
        this.dataNascita = dataNascita;
    }

    public Utente(String username, String nome, String cognome, String indirizzo, String email, String telefono, String dataNascita, String azienda, String indirizzoAzienda) {
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.indirizzo = indirizzo;
        this.email = email;
        this.telefono = telefono;
        this.dataNascita = dataNascita;
        this.azienda=azienda;
        this.indirizzoAzienda = indirizzoAzienda;
    }


    public Utente(String username, String nome, String cognome, String indirizzo, String telefono, String foto) {
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.indirizzo = indirizzo;
        this.telefono = telefono;
        this.foto = foto;
    }


    public Utente(String username, String nome, String cognome, String indirizzo, String email, String telefono, int confermato) {
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.indirizzo = indirizzo;
        this.email = email;
        this.telefono = telefono;
        this.confermato = confermato;
    }

    public Utente(){

    }




    public void addAzienda(String azienda){
        this.azienda=azienda;
    }

}

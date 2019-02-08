package it.uniba.di.sms.carpooling;

public class Azienda {

    private int id;
    private String nome, email, indirizzo;

    public Azienda(int id, String nome, String email, String indirizzo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.indirizzo = indirizzo;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }
}

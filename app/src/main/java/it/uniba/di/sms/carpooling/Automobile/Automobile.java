package it.uniba.di.sms.carpooling.Automobile;

public class Automobile {

    private int id, numPosti;
    private String nome, idUtente;

    public Automobile(int id, String nome, int numPosti, String idUtente) {
        this.id = id;
        this.numPosti = numPosti;
        this.nome = nome;
        this.idUtente = idUtente;
    }

    public Automobile() {
    }

    public int getNumPosti() {
        return numPosti;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getId() {
        return id;
    }


}

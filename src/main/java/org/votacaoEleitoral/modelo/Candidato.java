package org.votacaoEleitoral.modelo;

public class Candidato {

    private final int numero;
    private final String nome;
    private final Cargo cargo;

    public Candidato(final int numero, final String nome, final Cargo cargo) {
        this.numero = numero;
        this.nome = nome;
        this.cargo = cargo;
    }

    public int getNumero() {
        return numero;
    }

    public String getNome() {
        return nome;
    }

    public Cargo getCargo() {
        return cargo;
    }

    @Override
    public String toString() {
        return numero + " - " + nome;
    }

}

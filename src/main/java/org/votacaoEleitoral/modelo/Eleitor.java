package org.votacaoEleitoral.modelo;

import java.util.EnumSet;
import java.util.Set;

public class Eleitor {

    private final String cpf;
    private final String senha;
    private final Set<Cargo> cargosVotados = EnumSet.noneOf(Cargo.class);

    public Eleitor(final String cpf, final String senha) {
        this.cpf = cpf;
        this.senha = senha;
    }

    public String getSenha() {
        return senha;
    }

    public String getCpf() {
        return cpf;
    }

    public boolean senhaCorreta(final String senha) {
        return this.getSenha().equals(senha);
    }

    public void votarEm(final Cargo cargo) {
        this.cargosVotados.add(cargo);
    }

    public boolean votouEmTodosOsCargos() {
        return this.cargosVotados.size() == Cargo.values().length;
    }
}

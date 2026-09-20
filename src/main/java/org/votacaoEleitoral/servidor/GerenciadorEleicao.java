package org.votacaoEleitoral.servidor;

import org.votacaoEleitoral.modelo.Candidato;
import org.votacaoEleitoral.modelo.Cargo;
import org.votacaoEleitoral.modelo.Eleitor;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GerenciadorEleicao {

    private final Map<String, Eleitor> eleitoresPorCpf = new HashMap<>();
    private final Map<Cargo, List<Candidato>> candidatosPorCargo = new EnumMap<>(Cargo.class);
    private final Map<Cargo, Map<Integer, Integer>> votosPorCargo = new EnumMap<>(Cargo.class);

    public GerenciadorEleicao() {
        inicializarEleitoresDeTeste();
    }

    public boolean autenticar(final String cpf, final String senha) {
        Eleitor eleitor = eleitoresPorCpf.get(cpf);
        return eleitor != null && eleitor.senhaCorreta(senha);
    }

    private void inicializarEleitoresDeTeste() {
        eleitoresPorCpf.put("login1", new Eleitor("login1", "senha1"));
        eleitoresPorCpf.put("login2", new Eleitor("login2", "senha2"));
    }
}

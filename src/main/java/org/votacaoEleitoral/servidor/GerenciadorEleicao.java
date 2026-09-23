package org.votacaoEleitoral.servidor;

import org.votacaoEleitoral.modelo.Candidato;
import org.votacaoEleitoral.modelo.Cargo;
import org.votacaoEleitoral.modelo.Eleitor;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GerenciadorEleicao {

    private volatile boolean votacaoAberta = true; // Fix bug: flag compartilhada para controle de tempo entre Threads
    private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    private final Set<String> sessoesAtivas = new HashSet<>(); // Fix bug: controle de login duplicado
    private final Map<String, Eleitor> eleitoresPorCpf = new HashMap<>();
    private final Map<Cargo, List<Candidato>> candidatosPorCargo = new EnumMap<>(Cargo.class);
    private final Map<Cargo, Map<Integer, Integer>> votosPorCargo = new EnumMap<>(Cargo.class);

    public GerenciadorEleicao() {
        inicializarEleitoresDeTeste();
        inicializarCandidatosDeTeste();
        timer.schedule(this::encerrarVotacao, 30, TimeUnit.SECONDS);
    }

    private void encerrarVotacao (){
        votacaoAberta = false;
        timer.shutdown();
        System.out.println("Tempo esgotado. Votacao encerrada.");
    }

    public boolean isVotacaoAberta() {
        return votacaoAberta;
    }

    public synchronized boolean autenticar
            (final String cpf, final String senha) {
        Eleitor eleitor = this.eleitoresPorCpf.get(cpf);
        return eleitor != null && eleitor.senhaCorreta(senha);
    }

    public synchronized boolean registrarSessao(final String cpf){
        return sessoesAtivas.add(cpf);
    }

    public synchronized void encerrarSessao(final String cpf){
        sessoesAtivas.remove(cpf);
    }

    public synchronized void registrarVoto(final String cpf, final Cargo cargo, final int numeroCandidato) {
        Eleitor eleitor = this.eleitoresPorCpf.get(cpf);
        eleitor.votarEm(cargo);
        this.votosPorCargo.get(cargo).merge(numeroCandidato, 1, Integer::sum);
    }

    public synchronized boolean jaVotouEmTudo(final String cpf) {
        Eleitor eleitor = this.eleitoresPorCpf.get(cpf);
        return eleitor != null && eleitor.votouEmTodosOsCargos();
    }

    public synchronized Map<Integer, Integer> getResultado(final Cargo cargo) {
        // Fix bug: candidatos com zero votos não apareciam — agora itera pela lista de candidatos
        Map<Integer, Integer> resultado = new HashMap<>();
        List<Candidato> candidatos = this.candidatosPorCargo.get(cargo);

        for (Candidato candidato : candidatos) {
            int numero = candidato.getNumero();
            int votos = this.votosPorCargo.get(cargo).getOrDefault(numero, 0);
            resultado.put(numero, votos);
        }

        return resultado;
    }

    public synchronized List<Candidato> getCandidatos (final Cargo cargo) {
        return new ArrayList<>(this.candidatosPorCargo.get(cargo));
    }

    // ABAIXO EU DEIXEI DADOS DE TESTE - PODEMOS MELHORAR DEPOIS

    private void inicializarEleitoresDeTeste() {
        eleitoresPorCpf.put("login1", new Eleitor("login1", "senha1"));
        eleitoresPorCpf.put("login2", new Eleitor("login2", "senha2"));
    }

    private void inicializarCandidatosDeTeste() {
        for (Cargo cargo : Cargo.values()) {
            List<Candidato> candidatos = new ArrayList<>();

            switch (cargo) {
                case PRESIDENTE:
                    candidatos.add(new Candidato(13, "Lula (PT)", cargo));
                    candidatos.add(new Candidato(14, "Renan Santos (Missão)", cargo));
                    candidatos.add(new Candidato(16, "Hertz Dias (PSTU)", cargo));
                    candidatos.add(new Candidato(21, "Edmilson Costa (PCB)", cargo));
                    candidatos.add(new Candidato(22, "Flávio Bolsonaro (PL)", cargo));
                    candidatos.add(new Candidato(27, "Clariana Barão (DC)", cargo));
                    candidatos.add(new Candidato(28, "Pablo Marçal (PRTB)", cargo));
                    candidatos.add(new Candidato(29, "Rui Costa Pimenta (PCO)", cargo));
                    candidatos.add(new Candidato(30, "Romeu Zema (Novo)", cargo));
                    candidatos.add(new Candidato(35, "Wilson Grassi (Democrata)", cargo));
                    candidatos.add(new Candidato(55, "Ronaldo Caiado (PSD)", cargo));
                    candidatos.add(new Candidato(70, "Augusto Cury (Avante)", cargo));
                    candidatos.add(new Candidato(80, "Samara Martins (UP)", cargo));
                    break;

                case GOVERNADOR:
                    candidatos.add(new Candidato(12, "Juliana Brizola (PDT)", cargo));
                    candidatos.add(new Candidato(15, "Gabriel Souza (MDB)", cargo));
                    candidatos.add(new Candidato(16, "Rejane de Oliveira (PSTU)", cargo));
                    candidatos.add(new Candidato(22, "Zucco (PL)", cargo));
                    candidatos.add(new Candidato(29, "Cesar Pontes (PCO)", cargo));
                    candidatos.add(new Candidato(45, "Marcelo Maranata (PSDB)", cargo));
                    candidatos.add(new Candidato(80, "Priscila Voigt (UP)", cargo));
                    break;

                case SENADOR1:
                    candidatos.add(new Candidato(131, "Pimenta (PT)", cargo));
                    candidatos.add(new Candidato(151, "Rigotto (MDB)", cargo));
                    candidatos.add(new Candidato(160, "Daniela Mulheres Socialistas (PSTU)", cargo));
                    candidatos.add(new Candidato(161, "Regis Ethur (PSTU)", cargo));
                    candidatos.add(new Candidato(222, "Sanderson (PL)", cargo));
                    candidatos.add(new Candidato(234, "Renato Jaguarão (Cidadania)", cargo));
                    candidatos.add(new Candidato(290, "Ric Jones (PCO)", cargo));
                    candidatos.add(new Candidato(300, "Marcel Van Hattem (Novo)", cargo));
                    candidatos.add(new Candidato(455, "Milton Cardoso (PSDB)", cargo));
                    candidatos.add(new Candidato(500, "Manuela D'Ávila (PSOL)", cargo));
                    candidatos.add(new Candidato(800, "Luciano do MLB (UP)", cargo));
                    candidatos.add(new Candidato(808, "Tania Peres (UP)", cargo));
                    break;

                case SENADOR2:
                    candidatos.add(new Candidato(131, "Pimenta (PT)", cargo));
                    candidatos.add(new Candidato(151, "Rigotto (MDB)", cargo));
                    candidatos.add(new Candidato(160, "Daniela Mulheres Socialistas (PSTU)", cargo));
                    candidatos.add(new Candidato(161, "Regis Ethur (PSTU)", cargo));
                    candidatos.add(new Candidato(222, "Sanderson (PL)", cargo));
                    candidatos.add(new Candidato(234, "Renato Jaguarão (Cidadania)", cargo));
                    candidatos.add(new Candidato(290, "Ric Jones (PCO)", cargo));
                    candidatos.add(new Candidato(300, "Marcel Van Hattem (Novo)", cargo));
                    candidatos.add(new Candidato(455, "Milton Cardoso (PSDB)", cargo));
                    candidatos.add(new Candidato(500, "Manuela D'Ávila (PSOL)", cargo));
                    candidatos.add(new Candidato(800, "Luciano do MLB (UP)", cargo));
                    candidatos.add(new Candidato(808, "Tania Peres (UP)", cargo));
                    break;

                case DEPUTADO_FEDERAL:
                    // Candidatos de Esquerda
                    candidatos.add(new Candidato(1300, "Jonas Reis (PT)", cargo));
                    candidatos.add(new Candidato(1303, "Laura Sito (PT)", cargo));
                    candidatos.add(new Candidato(1313, "Alexandre Lindenmeyer (PT)", cargo));
                    candidatos.add(new Candidato(1314, "Elias Cabreira (PT)", cargo));
                    candidatos.add(new Candidato(1345, "Miriam Marroni (PT)", cargo));
                    candidatos.add(new Candidato(5000, "Roberto Robaina (PSOL)", cargo));
                    candidatos.add(new Candidato(5007, "Alisson Oliveira (PSOL)", cargo));
                    candidatos.add(new Candidato(5012, "John Elvis Braga (PSOL)", cargo));
                    candidatos.add(new Candidato(5050, "Fernanda Melchionna (PSOL)", cargo));
                    candidatos.add(new Candidato(1200, "Afonso Motta (PDT)", cargo));

                    // Candidatos de Direita
                    candidatos.add(new Candidato(2210, "Fernanda Barth (PL)", cargo));
                    candidatos.add(new Candidato(2214, "Marcelo Moraes (PL)", cargo));
                    candidatos.add(new Candidato(2221, "Alexandre Bobadra (PL)", cargo));
                    candidatos.add(new Candidato(2222, "Giovani Cherini (PL)", cargo));
                    candidatos.add(new Candidato(2228, "Felipe Pedri (PL)", cargo));
                    candidatos.add(new Candidato(2230, "Jessé Sangalli (PL)", cargo));
                    candidatos.add(new Candidato(2244, "Diogo Siqueira (PL)", cargo));
                    candidatos.add(new Candidato(3030, "Felipe Camozzato (NOVO)", cargo));
                    candidatos.add(new Candidato(3038, "Filipe Ilha (NOVO)", cargo));
                    candidatos.add(new Candidato(3075, "Eduardo Wartchow (NOVO)", cargo));
                    break;

                case DEPUTADO_ESTADUAL:
                    // Candidatos de Esquerda
                    candidatos.add(new Candidato(13010, "Alexandre Bublitz (PT)", cargo));
                    candidatos.add(new Candidato(13123, "Eva Valeria Lorenzato (PT)", cargo));
                    candidatos.add(new Candidato(13300, "Ericka Oliveira (PT)", cargo));
                    candidatos.add(new Candidato(13677, "Ivonete Carvalho (PT)", cargo));
                    candidatos.add(new Candidato(13813, "Ana Affonso (PT)", cargo));
                    candidatos.add(new Candidato(50000, "Luciana Genro (PSOL)", cargo));
                    candidatos.add(new Candidato(50023, "Abrão Godois (PSOL)", cargo));
                    candidatos.add(new Candidato(50111, "Fabiano Benites (PSOL)", cargo));
                    candidatos.add(new Candidato(50570, "Faby Gomes (PSOL)", cargo));
                    candidatos.add(new Candidato(50777, "Alice Carvalho (PSOL)", cargo));

                    // Candidatos de Direita
                    candidatos.add(new Candidato(22000, "Camila Nunes (PL)", cargo));
                    candidatos.add(new Candidato(22044, "Elvis Feltrin (PL)", cargo));
                    candidatos.add(new Candidato(22193, "Felipe Torres (PL)", cargo));
                    candidatos.add(new Candidato(22200, "Eliel Alves (PL)", cargo));
                    candidatos.add(new Candidato(22300, "Jonas Rodrigues (PL)", cargo));
                    candidatos.add(new Candidato(22470, "Joel Bolsonaro (PL)", cargo));
                    candidatos.add(new Candidato(30010, "Charles Oliveira (NOVO)", cargo));
                    candidatos.add(new Candidato(30019, "Claudia Lacerda (NOVO)", cargo));
                    candidatos.add(new Candidato(30369, "Cristian Paulo (NOVO)", cargo));
                    candidatos.add(new Candidato(30712, "Camila Willemberg (NOVO)", cargo));
                    break;

                default:
                    int numeroBase = 10;
                    candidatos.add(new Candidato(numeroBase, "Candidato A (" + cargo + ")", cargo));
                    candidatos.add(new Candidato(numeroBase + 1, "Candidato B (" + cargo + ")", cargo));
                    break;
            }

            candidatosPorCargo.put(cargo, candidatos);
            votosPorCargo.put(cargo, new HashMap<>());
        }
    }
}

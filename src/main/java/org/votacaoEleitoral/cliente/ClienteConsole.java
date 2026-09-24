package org.votacaoEleitoral.cliente;

import org.votacaoEleitoral.protocolo.Mensagem;
import org.votacaoEleitoral.protocolo.TipoMensagem;

import java.util.Scanner;

public class ClienteConsole {

    private static final int TOTAL_CARGOS = 6;

    private final ClienteRede rede;
    private final Scanner teclado;

    public ClienteConsole(final ClienteRede rede, final Scanner teclado) {
        this.rede = rede;
        this.teclado = teclado;
    }

    public void executar() {
        if (!fazerLogin()) {
            return;
        }

        // loop principal: requisição-resposta com o servidor
        while (true) {
            final Mensagem menu = rede.receberProximaMensagem();

            System.out.println("\nMenu: " + menu.getPayload());
            System.out.print("Escolha (1-Votar, 2-Resultados, outra tecla para sair): ");
            String escolha = teclado.nextLine().trim();

            if (!"1".equals(escolha) && !"2".equals(escolha)) {
                System.out.println("Encerrando sessão...");
                return;
            }

            final Mensagem resposta = rede.enviarEscolha(escolha);

            if (resposta.getTipo() == TipoMensagem.JA_VOTOU) {
                System.out.println(resposta.getPayload());
                continue;
            }

            if ("1".equals(escolha)) {
                conduzirVotacao(resposta);
            } else {
                exibirResultados(resposta);
            }
        }
    }

    private boolean fazerLogin() {
        System.out.print("CPF: ");
        final String cpf = teclado.nextLine().trim();

        System.out.print("Senha: ");
        final String senha = teclado.nextLine().trim();

        final Mensagem resposta = rede.login(cpf, senha);
        if (resposta.getTipo() == TipoMensagem.LOGIN_ERRO) {
            System.out.println("Falha no login: " + resposta.getPayload());
            return false;
        }
        System.out.println(resposta.getPayload());
        return true;
    }

    private void conduzirVotacao(final Mensagem primeiraMensagem) {
        Mensagem atual = primeiraMensagem;
        String cargo = "";

        // loop de votação: VOTACAO_ERRO mantém o loop sem desincronizar cliente/servidor
        while (atual.getTipo() == TipoMensagem.VOTACAO || atual.getTipo() == TipoMensagem.VOTACAO_ERRO) {

            if (atual.getTipo() == TipoMensagem.VOTACAO) {
                cargo = atual.getPayload();
                System.out.println("\n== Votação: " + cargo + " ==");
            } else {
                System.out.println("Erro: " + atual.getPayload() + ". Tente novamente.");
            }

            System.out.print("Número do candidato: ");

            int numero;
            try {
                numero = Integer.parseInt(teclado.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite apenas números.");
                atual = new Mensagem(TipoMensagem.VOTACAO_ERRO, "Entrada invalida");
                continue;
            }

            Mensagem respostaVoto = rede.enviarVoto(numero);

            if (respostaVoto.getTipo() == TipoMensagem.VOTO_OK) {
                System.out.println(respostaVoto.getPayload());
                atual = rede.receberProximaMensagem();
            } else {
                atual = respostaVoto;
            }
        }

        if (atual.getTipo() == TipoMensagem.VOTACAO_CONCLUIDA) {
            System.out.println("\n" + atual.getPayload());
        }
    }

    private void exibirResultados(final Mensagem primeiraMensagem) {
        System.out.println("\n=== Resultado ===");
        Mensagem atual = primeiraMensagem;

        for (int i = 0; i < TOTAL_CARGOS; i++) {
            final String[] campos = atual.getCampos();
            System.out.println(campos[0] + ":");

            for (int j = 1; j < campos.length; j++) {
                System.out.println("  " + campos[j] + " votos");
            }

            if (i < TOTAL_CARGOS - 1) {
                atual = rede.receberProximaMensagem();
            }
        }
    }
}

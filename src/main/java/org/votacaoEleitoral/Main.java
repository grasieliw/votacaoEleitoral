package org.votacaoEleitoral;
import org.votacaoEleitoral.servidor.ConexaoCliente;
import org.votacaoEleitoral.servidor.GerenciadorEleicao;

import java.net.*;

public class Main {
    public static void main(String[] args) {
        int porta = 8080;

        GerenciadorEleicao gerenciador = new GerenciadorEleicao();

        // ServerSocket: escuta conexões na porta 8080
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            // setSoTimeout: destrava accept() periodicamente para checar fim da votação
            serverSocket.setSoTimeout(1000);
            System.out.println("O servidor foi aberto na porta: " + porta);

            // servidor concorrente: cada cliente ganha sua própria thread
            while (gerenciador.isVotacaoAberta()) {
                try {
                    Socket socketCliente = serverSocket.accept();
                    ConexaoCliente conexao = new ConexaoCliente(socketCliente, gerenciador);
                    new Thread(conexao).start();
                } catch (SocketTimeoutException e) {
                    // destrava accept(), volta ao while para checar isVotacaoAberta()
                }
            }

            System.out.println("Servidor encerrado. Votacao finalizada.");

        } catch (Exception e) {
            System.out.println("Erro ao abrir o servidor: " + e.getMessage());
        }
    }
}

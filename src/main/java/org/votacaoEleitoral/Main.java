package org.votacaoEleitoral;
import org.votacaoEleitoral.servidor.ConexaoCliente;
import org.votacaoEleitoral.servidor.GerenciadorEleicao;

import java.net.*;

public class Main {
    public static void main(String[] args) {
        int porta = 8080;

        GerenciadorEleicao gerenciador = new GerenciadorEleicao();

        // Bug fix: try-with-resources garante que o ServerSocket seja fechado ao encerrar
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            // Bug fix: setSoTimeout destrava o accept() a cada 1s para checar isVotacaoAberta()
            serverSocket.setSoTimeout(1000);
            System.out.println("O servidor foi aberto na porta: " + porta);

            while (gerenciador.isVotacaoAberta()) {
                try {
                    Socket socketCliente = serverSocket.accept();
                    ConexaoCliente conexao = new ConexaoCliente(socketCliente, gerenciador);
                    new Thread(conexao).start();
                } catch (SocketTimeoutException e) {
                    // timeout do accept() — checa isVotacaoAberta() na proxima iteracao do while
                }
            }

            System.out.println("Servidor encerrado. Votacao finalizada.");

        } catch (Exception e) {
            System.out.println("Erro ao abrir o servidor: " + e.getMessage());
        }
    }
}
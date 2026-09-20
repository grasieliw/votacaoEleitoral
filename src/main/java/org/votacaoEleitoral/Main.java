package org.votacaoEleitoral;
import org.votacaoEleitoral.servidor.ConexaoCliente;
import org.votacaoEleitoral.servidor.GerenciadorEleicao;

import java.net.*;

public class Main {
    public static void main(String[] args) {
        int porta = 8080;
        long tempoInicio = System.currentTimeMillis();
        long tempoLimite = 10 * 60 * 1000;

        GerenciadorEleicao gerenciador = new GerenciadorEleicao();

        try {
            ServerSocket serverSocket = new ServerSocket(porta);
            System.out.println("O servidor foi aberto na porta: " + porta);

            while ((System.currentTimeMillis() - tempoInicio) <= tempoLimite) {
                Socket socketCliente = serverSocket.accept();
                ConexaoCliente conexao = new ConexaoCliente(socketCliente, gerenciador);
                Thread novaThread = new Thread(conexao);
                novaThread.start();
            }

        } catch (Exception e) {
            System.out.println("Erro ao abrir o servidor: " + e.getMessage());
        }
    }
}

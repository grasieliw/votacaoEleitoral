package org.votacaoEleitoral;
import java.net.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        int porta = 8080;
        long tempoInicio = System.currentTimeMillis();
        long tempoLimite = 10 * 60 * 1000;

        try {
            ServerSocket serverSocket = new ServerSocket(porta);
            System.out.println("O servidor foi aberto na porta: " + porta);

            while ((System.currentTimeMillis() - tempoInicio) <= tempoLimite) {
                Socket socketCliente = serverSocket.accept();
                ConexaoCliente conexao = new ConexaoCliente(socketCliente);
                Thread novaThread = new Thread(conexao);
                novaThread.start();
            }

        } catch (Exception e) {
            System.out.println("Erro ao abrir o servidor: " + e.getMessage());
        }
    }
}

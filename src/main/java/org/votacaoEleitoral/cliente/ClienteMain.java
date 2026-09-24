package org.votacaoEleitoral.cliente;

import java.util.Scanner;

public class ClienteMain {

    public static void main(String[] args) {
        final Scanner teclado = new Scanner(System.in);

        // default localhost: servidor e cliente rodam na mesma máquina
        final String ip = args.length > 0 ? args[0] : "localhost";
        final int porta = args.length > 1 ? Integer.parseInt(args[1]) : 8080;

        // try-with-resources: fecha socket automaticamente ao encerrar
        try (ClienteRede rede = new ClienteRede(ip, porta)) {
            System.out.println("Conectado ao servidor " + ip + ":" + porta);
            new ClienteConsole(rede, teclado).executar();
        } catch (Exception e) {
            System.out.println("Não foi possível conectar ao servidor: " + e.getMessage());
        }
    }
}

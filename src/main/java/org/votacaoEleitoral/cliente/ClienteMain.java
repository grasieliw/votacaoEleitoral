package org.votacaoEleitoral.cliente;

import java.util.Scanner;

public class ClienteMain {

    public static void main(String[] args) {
        final Scanner teclado = new Scanner(System.in);

        final String ip = args.length > 0 ? args[0] : perguntar(teclado, "IP do servidor: ");
        final int porta = args.length > 1 ? Integer.parseInt(args[1]) : 8080;

        try (ClienteRede rede = new ClienteRede(ip, porta)) {
            System.out.println("Conectado ao servidor " + ip + ":" + porta);
            new ClienteConsole(rede, teclado).executar();
        } catch (Exception e) {
            System.out.println("Não foi possível conectar ao servidor: " + e.getMessage());
        }
    }

    private static String perguntar(final Scanner teclado, final String pergunta) {
        System.out.print(pergunta);
        return teclado.nextLine().trim();
    }
}
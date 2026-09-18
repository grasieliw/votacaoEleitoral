package org.votacaoEleitoral;
import java.net.*;
import java.io.*;

public class ConexaoCliente implements Runnable {
    private Socket socket;
    private String idCliente;

    ConexaoCliente(Socket socketCliente) {
        this.socket = socketCliente;
    }
    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            PrintWriter saida = new PrintWriter(this.socket.getOutputStream(), true);
            String login = entrada.readLine();
            String[] dadosCliente = login.split(",");
            if (dadosCliente[0].equals("login1") && dadosCliente[1].equals("senha1")) {
                idCliente = dadosCliente[0];
                saida.println("Usuario autenticado");

                //Acho que aqui fica sua parte amg
                //Foi bem de boas fazer a minha parte, se precisar de ajuda pode chamar :)

            }else if(dadosCliente[0].equals("login2") && dadosCliente[1].equals("senha2")){
                idCliente = dadosCliente[0];
                saida.println("Usuario autenticado");
            }
            else {
                saida.println("Errou xexelento"); // Obviamente essa mensagem tem que mudar, só botei pra fazer vc rir kkkk
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

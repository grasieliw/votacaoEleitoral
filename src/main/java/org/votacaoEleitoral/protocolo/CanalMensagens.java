package org.votacaoEleitoral.protocolo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CanalMensagens {
    private final Socket socket;
    // BufferedReader: lê stream de bytes do socket como texto, readLine() usa \n como terminador
    private final BufferedReader entrada;
    // PrintWriter: escreve texto no socket, auto-flush envia imediatamente sem buffer
    private final PrintWriter saida;

    public CanalMensagens(final Socket socket) throws IOException {
        this.socket = socket;
        this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.saida = new PrintWriter(socket.getOutputStream(), true);
    }

    // serializar + println: objeto → texto + \n → socket
    public void enviar(final Mensagem mensagem) {
        this.saida.println(mensagem.serializar());
    }

    // readLine: lê até \n → parsear: texto → objeto
    public Mensagem receber() throws IOException {
        final String linha = this.entrada.readLine();

        if (linha == null) {
            // null = FIN recebido, conexão encerrada pelo outro lado
            throw new IOException("Conexao encerrada pelo outro lado");
        }
        return Mensagem.parsear(linha);
    }

    public void close() {
        try {
            this.socket.close();
        } catch (IOException ignored) {
        }
    }
}

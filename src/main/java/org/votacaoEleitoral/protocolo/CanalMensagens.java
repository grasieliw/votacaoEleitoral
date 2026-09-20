package org.votacaoEleitoral.protocolo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CanalMensagens {
    private final Socket socket;
    private final BufferedReader entrada;
    private final PrintWriter saida;

    public CanalMensagens(final Socket socket) throws IOException {
        this.socket= socket;
        this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.saida = new PrintWriter(socket.getOutputStream(), true);
    }

    public void enviar(final Mensagem mensagem) {
        this.saida.println(mensagem.serializar());
    }

    public Mensagem receber() throws IOException {
        final String linha = this.entrada.readLine();

        if (linha == null) {
            throw new IOException("Conexao encerrada pelo outro lado");
        }
        return Mensagem.parsear(linha);
    }

    public void close() {
        try {
            this.socket.close();
        } catch (IOException ignored) {
            // no-op
        }
    }

}

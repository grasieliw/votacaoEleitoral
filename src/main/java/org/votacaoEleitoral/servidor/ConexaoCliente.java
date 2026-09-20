package org.votacaoEleitoral.servidor;
import org.votacaoEleitoral.protocolo.CanalMensagens;
import org.votacaoEleitoral.protocolo.Mensagem;
import org.votacaoEleitoral.protocolo.TipoMensagem;

import java.net.*;
import java.io.*;

public class ConexaoCliente implements Runnable {
    private final GerenciadorEleicao gerenciador;
    private CanalMensagens canal;
    private String cpfLogado;

    public ConexaoCliente(final Socket socket, final GerenciadorEleicao gerenciador) throws IOException {
        this.canal = new CanalMensagens(socket);
        this.gerenciador = gerenciador;
    }

    public void run() {

        try {
            if (!autenticar()) {
                return;
            }

            canal.enviar(new Mensagem(TipoMensagem.MENU, "1-Votar,2-Resultados"));


        } catch (final IOException ex) {
            System.out.println("Cliente " + (cpfLogado != null ? cpfLogado : "?") + " desconectou: " + ex.getMessage());
        } finally {
            canal.close();
        }
    }

    private boolean autenticar() throws IOException {
        Mensagem login = canal.receber();
        String[] campos = login.getCampos();

        if (campos.length < 2) {
            canal.enviar(new Mensagem(TipoMensagem.LOGIN_ERRO, "Mensagem de login invalida"));
            return false;
        }

        String cpf = campos[0];
        String senha = campos[1];

        if (gerenciador.autenticar(cpf, senha)) {
            this.cpfLogado = cpf;
            canal.enviar(new Mensagem(TipoMensagem.LOGIN_OK, "Usuario autenticado"));
            return true;
        } else {
            canal.enviar(new Mensagem(TipoMensagem.LOGIN_ERRO, "Login ou senha invalidos"));
            return false;
        }
    }
}

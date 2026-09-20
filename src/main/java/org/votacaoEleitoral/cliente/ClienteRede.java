package org.votacaoEleitoral.cliente;

import org.votacaoEleitoral.protocolo.CanalMensagens;
import org.votacaoEleitoral.protocolo.Mensagem;
import org.votacaoEleitoral.protocolo.TipoMensagem;

import java.io.IOException;
import java.net.Socket;

public class ClienteRede implements AutoCloseable {

    private final CanalMensagens canal;

    public ClienteRede(final String ip, final int porta) throws IOException {
        Socket socket = new Socket(ip, porta);
        this.canal = new CanalMensagens(socket);
    }

    public Mensagem login(final String cpf, final String senha) {
        canal.enviar(new Mensagem(TipoMensagem.LOGIN, cpf + ";" + senha));
        return receberOuFalhar();
    }

    public Mensagem enviarEscolha(final String escolha) {
        canal.enviar(new Mensagem(TipoMensagem.ESCOLHA, escolha));
        return receberOuFalhar();
    }

    public Mensagem enviarVoto(final int numeroCandidato) {
        canal.enviar(new Mensagem(TipoMensagem.VOTO, String.valueOf(numeroCandidato)));
        return receberOuFalhar();
    }

    public Mensagem receberProximaMensagem() {
        return receberOuFalhar();
    }

    private Mensagem receberOuFalhar() {
        try {
            return canal.receber();
        } catch (IOException e) {
            throw new RuntimeException("Conexao com o servidor perdida: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        canal.close();
    }
}
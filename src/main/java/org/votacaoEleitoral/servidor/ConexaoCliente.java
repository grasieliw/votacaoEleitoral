package org.votacaoEleitoral.servidor;

import org.votacaoEleitoral.modelo.Candidato;
import org.votacaoEleitoral.modelo.Cargo;
import org.votacaoEleitoral.protocolo.CanalMensagens;
import org.votacaoEleitoral.protocolo.Mensagem;
import org.votacaoEleitoral.protocolo.TipoMensagem;

import java.net.*;
import java.io.*;
import java.util.List;
import java.util.Map;

// Runnable: cada instância roda em sua própria thread, uma por cliente
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

            // loop principal: encerra quando votação fechar
            while (gerenciador.isVotacaoAberta()) {
                canal.enviar(new Mensagem(TipoMensagem.MENU, "1-Votar,2-Resultados"));
                Mensagem escolha = canal.receber();

                if ("1".equals(escolha.getPayload())) {
                    if (gerenciador.jaVotouEmTudo(cpfLogado)) {
                        canal.enviar(new Mensagem(TipoMensagem.JA_VOTOU, "Votacao ja finalizada!"));
                    } else if (!gerenciador.isVotacaoAberta()) {
                        canal.enviar(new Mensagem(TipoMensagem.JA_VOTOU, "Votacao encerrada. Tempo esgotado."));
                    } else {
                        this.processarVotacao();
                    }
                } else if ("2".equals(escolha.getPayload())) {
                    this.processarResultados();
                } else {
                    canal.enviar(new Mensagem(TipoMensagem.ESCOLHA_ERRO, "Opcao invalida"));
                    continue;
                }
            }

        } catch (final IOException ex) {
            System.out.println("Cliente " + (cpfLogado != null ? cpfLogado : "?") + " desconectou: " + ex.getMessage());
        } finally {
            // finally: libera sessão e fecha socket independente do motivo de encerramento
            if (cpfLogado != null) {
                gerenciador.encerrarSessao(cpfLogado);
            }
            canal.close();
        }
    }

    private boolean autenticar() throws IOException {
        Mensagem login = canal.receber();
        String[] campos = login.getCampos();

        // valida formato: LOGIN|cpf;senha
        if (campos.length < 2) {
            canal.enviar(new Mensagem(TipoMensagem.LOGIN_ERRO, "Mensagem de login invalida"));
            return false;
        }

        String cpf = campos[0];
        String senha = campos[1];

        if (gerenciador.autenticar(cpf, senha)) {
            // controle de sessão: impede login duplicado
            if (gerenciador.registrarSessao(cpf)) {
                this.cpfLogado = cpf;
                canal.enviar(new Mensagem(TipoMensagem.LOGIN_OK, "Usuario autenticado"));
                return true;
            } else {
                canal.enviar(new Mensagem(TipoMensagem.LOGIN_ERRO, "CPF ja esta em uso"));
                return false;
            }
        } else {
            canal.enviar(new Mensagem(TipoMensagem.LOGIN_ERRO, "Login ou senha invalidos"));
            return false;
        }
    }

    private void processarVotacao() throws IOException {
        if (!gerenciador.isVotacaoAberta()) {
            canal.enviar(new Mensagem(TipoMensagem.JA_VOTOU, "Votacao encerrada. Tempo esgotado."));
            return;
        }
        // requisição-resposta: servidor envia cargo, cliente responde com número
        for (Cargo cargo : Cargo.values()) {
            canal.enviar(new Mensagem(TipoMensagem.VOTACAO, cargo.name()));
            boolean votoValido = false;

            while (!votoValido) {
                Mensagem voto = canal.receber();
                int numeroCandidato;
                try {
                    numeroCandidato = Integer.parseInt(voto.getPayload().trim());
                } catch (NumberFormatException e) {
                    canal.enviar(new Mensagem(TipoMensagem.VOTACAO_ERRO, "Digite apenas numeros"));
                    continue;
                }

                List<Candidato> candidatos = gerenciador.getCandidatos(cargo);
                boolean candidatoValido = candidatos.stream().anyMatch(c -> c.getNumero() == numeroCandidato);

                if (candidatoValido) {
                    gerenciador.registrarVoto(cpfLogado, cargo, numeroCandidato);
                    canal.enviar(new Mensagem(TipoMensagem.VOTO_OK, "Voto computado!"));
                    votoValido = true;
                } else {
                    canal.enviar(new Mensagem(TipoMensagem.VOTACAO_ERRO, "Candidato nao encontrado"));
                }
            }
        }

        canal.enviar(new Mensagem(TipoMensagem.VOTACAO_CONCLUIDA, "Votacao concluida com sucesso!"));
    }

    private void processarResultados() {
        if (!gerenciador.jaVotouEmTudo(cpfLogado)) {
            canal.enviar(new Mensagem(TipoMensagem.JA_VOTOU, "O resultado dos candidatos sera exibido so para aqueles que votarem!"));
        } else {
            enviarResultadoCompleto();
        }
    }

    private void enviarResultadoCompleto() {
        for (Cargo cargo : Cargo.values()) {
            Map<Integer, Integer> resultado = gerenciador.getResultado(cargo);
            StringBuilder payload = new StringBuilder(cargo.name());
            resultado.forEach((numero, votos) -> payload.append(";").append(numero).append("=").append(votos));
            canal.enviar(new Mensagem(TipoMensagem.RESULTADO, payload.toString()));
        }
    }
}

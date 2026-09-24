package org.votacaoEleitoral.protocolo;

public class Mensagem {
    // protocolo: formato TIPO|payload, campos separados por ;
    private static final String SEPARADOR_TIPO = "\\|";

    private final TipoMensagem tipo;
    private final String payload;

    public Mensagem(final TipoMensagem tipo, final String payload) {
        this.tipo = tipo;
        this.payload = payload == null ? "" : payload;
    }

    public TipoMensagem getTipo() {
        return tipo;
    }

    public String getPayload() {
        return payload;
    }

    public String[] getCampos() {
        return payload.isEmpty() ? new String[0] : payload.split(";");
    }

    // objeto → "TIPO|payload"
    public String serializar() {
        return tipo.name() + "|" + payload;
    }

    // "TIPO|payload" → objeto
    public static Mensagem parsear(final String linha) {
        if (linha == null || linha.isEmpty()) {
            throw new IllegalArgumentException("Linha vazia nao é uma mensagem valida");
        }

        // limite 2: preserva | dentro do payload
        String[] partes = linha.split(SEPARADOR_TIPO, 2);
        TipoMensagem tipo = TipoMensagem.valueOf(partes[0]);
        String payload = partes.length > 1 ? partes[1] : "";
        return new Mensagem(tipo, payload);
    }

    @Override
    public String toString() {
        return serializar();
    }
}

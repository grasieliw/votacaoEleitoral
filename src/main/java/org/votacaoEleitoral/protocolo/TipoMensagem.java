package org.votacaoEleitoral.protocolo;

public enum TipoMensagem {
    LOGIN,               // Cliente -> Servidor : "cpf;senha"
    LOGIN_OK,            // Servidor -> Cliente : "Usuario autenticado"
    LOGIN_ERRO,          // Servidor -> Cliente : "Login ou senha invalidos"
    MENU,                // Servidor -> Cliente : "1-Votar,2-Resultados"
    ESCOLHA,             // Cliente -> Servidor : "1" ou "2"
    ESCOLHA_ERRO,        // Servidor -> Cliente : "Escolha invalida"
    VOTACAO,             // Servidor -> Cliente : "CARGO"
    VOTO,                // Cliente -> Servidor : "numero"
    VOTACAO_ERRO,        // Servidor -> Cliente : "Candidato nao encontrado"
    VOTACAO_CONCLUIDA,   // Servidor -> Cliente : "Votacao concluida com sucesso!"
    JA_VOTOU,            // Servidor -> Cliente : mensagem dizendo que precisa votar antes de ver o resultado
    RESULTADO            // Servidor -> Cliente : "CARGO;numero=votos;numero=votos;..."
}

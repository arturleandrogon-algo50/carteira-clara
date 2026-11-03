package br.com.carteiraclara.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StatusControllerTest {

    private final StatusController controller = new StatusController();

    @Test
    void statusIndicaQueAAplicacaoEstaNoAr() {
        var resposta = controller.status();

        assertThat(resposta.aplicacao()).isEqualTo("carteira-clara");
        assertThat(resposta.situacao()).isEqualTo("no ar");
    }

    @Test
    void statusSempreInformaQueNaoEConsultoriaFinanceira() {
        var resposta = controller.status();

        assertThat(resposta.aviso()).contains("Não é recomendação de investimento");
    }
}

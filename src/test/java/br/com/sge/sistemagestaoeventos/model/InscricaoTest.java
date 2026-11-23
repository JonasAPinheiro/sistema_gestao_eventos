package br.com.sge.sistemagestaoeventos.model;

import br.com.sge.sistemagestaoeventos.exception.StatusInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InscricaoTest {

    @Test
    void inscricaoPendenteNaoPodeSerReembolsada() {
        Inscricao inscricao = new Inscricao("evento-1", "participante-1", new BigDecimal("80.00"));

        assertThatThrownBy(inscricao::validarReembolsavel)
                .isInstanceOf(StatusInvalidoException.class)
                .hasMessage("Somente inscrições confirmadas podem ser reembolsadas.");
    }

    @Test
    void registraPagamentoComCarteira() {
        Inscricao inscricao = new Inscricao("evento-1", "participante-1", new BigDecimal("80.00"));

        inscricao.registrarPagamentoCarteira(new BigDecimal("30"));

        assertThat(inscricao.getValorPagoCarteira()).isEqualByComparingTo("30.00");
    }

    @Test
    void inscricaoNaoPendenteNaoPodeMudarDeEstado() {
        Inscricao inscricao = new Inscricao("evento-1", "participante-1", new BigDecimal("80.00"));
        inscricao.confirmar();

        assertThatThrownBy(() -> inscricao.cancelar("Desistência"))
                .isInstanceOf(StatusInvalidoException.class)
                .hasMessage("Somente inscrições pendentes podem mudar de estado.");
    }
}

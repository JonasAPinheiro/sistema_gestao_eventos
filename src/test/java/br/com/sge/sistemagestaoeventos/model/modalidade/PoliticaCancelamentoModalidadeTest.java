package br.com.sge.sistemagestaoeventos.model.modalidade;

import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PoliticaCancelamentoModalidadeTest {

    private static final LocalDateTime INICIO_EVENTO = LocalDateTime.of(2026, 10, 10, 18, 0);

    @Test
    void modalidadeAbertaPermiteCancelamentoAntesDoEvento() {
        assertThatCode(() -> new ModalidadeAberta().validarCancelamento(
                INICIO_EVENTO, INICIO_EVENTO.minusMinutes(1), null
        )).doesNotThrowAnyException();
    }

    @Test
    void modalidadeAbertaRecusaCancelamentoNoInicioDoEvento() {
        ModalidadeAberta modalidade = new ModalidadeAberta();

        assertThatThrownBy(() -> modalidade.validarCancelamento(INICIO_EVENTO, INICIO_EVENTO,null)
        ).isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void modalidadeExclusivaAceitaCancelamentoComExatamente24HorasDeAntecedencia() {
        assertThatCode(() -> new ModalidadeExclusivaParaAlunos().validarCancelamento(
                INICIO_EVENTO, INICIO_EVENTO.minusHours(24), null
        )).doesNotThrowAnyException();
    }

    @Test
    void modalidadeExclusivaRecusaCancelamentoComMenosDe24HorasDeAntecedencia() {
        LocalDateTime momentoCancelamento = INICIO_EVENTO.minusHours(24).plusNanos(1);

        ModalidadeExclusivaParaAlunos modalidade = new ModalidadeExclusivaParaAlunos();

        assertThatThrownBy(() ->
                modalidade.validarCancelamento(INICIO_EVENTO, momentoCancelamento, null)
        ).isInstanceOf(RegraNegocioException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void modalidadeComRestricaoDeIdadeExigeMotivo(String motivo) {
        ModalidadeComRestricaoDeIdade modalidade =
                new ModalidadeComRestricaoDeIdade(18);

        assertThatThrownBy(() ->
                validarCancelamentoComMotivo(modalidade, motivo)
        )
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("O motivo do cancelamento é obrigatório.");
    }

    @Test
    void modalidadeComRestricaoDeIdadePermiteCancelamentoComMotivo() {
        ModalidadeComRestricaoDeIdade modalidade = new ModalidadeComRestricaoDeIdade(18);

        assertThatCode(() -> modalidade.validarCancelamento(
                INICIO_EVENTO, INICIO_EVENTO.plusDays(1), "Solicitação do participante"
        )).doesNotThrowAnyException();
    }

    private void validarCancelamentoComMotivo(
            ModalidadeComRestricaoDeIdade modalidade,
            String motivo) {

        modalidade.validarCancelamento(
                INICIO_EVENTO,
                INICIO_EVENTO.plusDays(1),
                motivo
        );
    }
}
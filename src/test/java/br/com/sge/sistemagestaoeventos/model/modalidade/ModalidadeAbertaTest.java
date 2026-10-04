package br.com.sge.sistemagestaoeventos.model.modalidade;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModalidadeAbertaTest {

    private final ModalidadeAberta modalidade = new ModalidadeAberta();

    @Test
    @DisplayName("Deve retornar tipo aberto")
    void deveRetornarTipoAberto() {

        assertThat(modalidade.getTipo())
                .isEqualTo(TipoModalidade.ABERTO);
    }

    @Test
    @DisplayName("Deve retornar comprovante simples para eventos abertos")
    void deveRetornarComprovanteSimples() {

        assertThat(modalidade.getTipoComprovante())
                .isEqualTo(TipoComprovante.SIMPLES);
    }

    @Test
    @DisplayName("Deve permitir cancelamento antes do início do evento")
    void devePermitirCancelamentoAntesDoInicio() {

        LocalDateTime inicioEvento =
                LocalDateTime.of(2026, 11, 10, 19, 0);

        LocalDateTime momentoCancelamento =
                LocalDateTime.of(2026, 11, 10, 18, 0);

        assertThatCode(() ->
                modalidade.validarCancelamento(
                        inicioEvento,
                        momentoCancelamento,
                        "Imprevisto"
                )
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve rejeitar cancelamento após o início do evento")
    void deveRejeitarCancelamentoAposInicio() {

        LocalDateTime inicioEvento =
                LocalDateTime.of(2026, 11, 10, 19, 0);

        LocalDateTime momentoCancelamento =
                LocalDateTime.of(2026, 11, 10, 19, 30);

        assertThatThrownBy(() ->
                modalidade.validarCancelamento(
                        inicioEvento,
                        momentoCancelamento,
                        "Imprevisto"
                )
        )
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "Não é possível cancelar a inscrição após o início do evento."
                );
    }
}

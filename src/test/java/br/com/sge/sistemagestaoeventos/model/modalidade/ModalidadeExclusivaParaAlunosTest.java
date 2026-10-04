package br.com.sge.sistemagestaoeventos.model.modalidade;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Participante;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModalidadeExclusivaParaAlunosTest {

    private final ModalidadeExclusivaParaAlunos modalidade =
            new ModalidadeExclusivaParaAlunos();

    @Test
    @DisplayName("Deve permitir inscrição de participante com matrícula ativa")
    void devePermitirParticipanteComMatriculaAtiva() {
        Participante participante = criarParticipante("12345");

        assertThatCode(() ->
                modalidade.validarElegibilidade(participante)
        ).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Deve rejeitar participante sem matrícula válida")
    void deveRejeitarParticipanteSemMatriculaValida(String matricula) {
        Participante participante = criarParticipante(matricula);

        assertThatThrownBy(() ->
                modalidade.validarElegibilidade(participante)
        )
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "O participante deve possuir uma matrícula ativa para se inscrever."
                );
    }

    @Test
    @DisplayName("Deve rejeitar participante com matrícula inativa")
    void deveRejeitarParticipanteComMatriculaInativa() {
        Participante participante = criarParticipante("12345");
        participante.inativarMatricula();

        assertThatThrownBy(() ->
                modalidade.validarElegibilidade(participante)
        )
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "O participante deve possuir uma matrícula ativa para se inscrever."
                );
    }

    @Test
    @DisplayName("Deve retornar tipo exclusivo para alunos")
    void deveRetornarTipoExclusivoParaAlunos() {
        assertThat(modalidade.getTipo())
                .isEqualTo(TipoModalidade.EXCLUSIVO_ALUNOS);
    }

    @Test
    @DisplayName("Deve retornar comprovante digital completo")
    void deveRetornarComprovanteDigitalCompleto() {
        assertThat(modalidade.getTipoComprovante())
                .isEqualTo(TipoComprovante.DIGITAL_COMPLETO);
    }

    private static Participante criarParticipante(String matricula) {
        return new Participante(
                matricula,
                "Maria Silva",
                "maria@email.com",
                LocalDate.of(1995, 1, 1)
        );
    }
}
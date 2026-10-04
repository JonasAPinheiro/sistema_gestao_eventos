package br.com.sge.sistemagestaoeventos.model.modalidade;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Participante;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModalidadeComRestricaoDeIdadeTest {

    @Test
    @DisplayName("Deve criar modalidade com idade mínima válida")
    void deveCriarModalidadeComIdadeMinimaValida() {
        ModalidadeComRestricaoDeIdade modalidade =
                new ModalidadeComRestricaoDeIdade(18);

        assertThat(modalidade.getIdadeMinima())
                .isEqualTo(18);
    }

    @Test
    @DisplayName("Deve rejeitar idade mínima nula")
    void deveRejeitarIdadeMinimaNula() {
        assertThatThrownBy(() ->
                new ModalidadeComRestricaoDeIdade(null)
        )
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "A idade mínima do evento deve ser um número inteiro maior que zero."
                );
    }

    @Test
    @DisplayName("Deve rejeitar idade mínima igual a zero")
    void deveRejeitarIdadeMinimaIgualAZero() {
        assertThatThrownBy(() ->
                new ModalidadeComRestricaoDeIdade(0)
        )
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "A idade mínima do evento deve ser um número inteiro maior que zero."
                );
    }

    @Test
    @DisplayName("Deve rejeitar idade mínima negativa")
    void deveRejeitarIdadeMinimaNegativa() {
        assertThatThrownBy(() ->
                new ModalidadeComRestricaoDeIdade(-1)
        )
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "A idade mínima do evento deve ser um número inteiro maior que zero."
                );
    }

    @Test
    @DisplayName("Deve permitir participante com idade exatamente igual à mínima")
    void devePermitirIdadeExatamenteIgualAMinima() {
        ModalidadeComRestricaoDeIdade modalidade =
                new ModalidadeComRestricaoDeIdade(18);

        Participante participante = criarParticipante(
                LocalDate.now().minusYears(18)
        );

        assertThatCode(() ->
                modalidade.validarElegibilidade(participante)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve permitir participante acima da idade mínima")
    void devePermitirParticipanteAcimaDaIdadeMinima() {
        ModalidadeComRestricaoDeIdade modalidade =
                new ModalidadeComRestricaoDeIdade(18);

        Participante participante = criarParticipante(
                LocalDate.now().minusYears(25)
        );

        assertThatCode(() ->
                modalidade.validarElegibilidade(participante)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve rejeitar participante abaixo da idade mínima")
    void deveRejeitarParticipanteAbaixoDaIdadeMinima() {
        ModalidadeComRestricaoDeIdade modalidade =
                new ModalidadeComRestricaoDeIdade(18);

        Participante participante = criarParticipante(
                LocalDate.now().minusYears(17)
        );

        assertThatThrownBy(() ->
                modalidade.validarElegibilidade(participante)
        )
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "O participante não possui a idade mínima exigida para este evento."
                );
    }

    @Test
    @DisplayName("Deve rejeitar participante sem data de nascimento")
    void deveRejeitarParticipanteSemDataDeNascimento() {
        ModalidadeComRestricaoDeIdade modalidade =
                new ModalidadeComRestricaoDeIdade(18);

        Participante participante = criarParticipante(null);

        assertThatThrownBy(() ->
                modalidade.validarElegibilidade(participante)
        )
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "O participante deve possuir data de nascimento informada."
                );
    }

    @Test
    @DisplayName("Deve retornar tipo com restrição de idade")
    void deveRetornarTipoRestricaoDeIdade() {
        ModalidadeComRestricaoDeIdade modalidade =
                new ModalidadeComRestricaoDeIdade(18);

        assertThat(modalidade.getTipo())
                .isEqualTo(TipoModalidade.RESTRICAO_IDADE);
    }

    @Test
    @DisplayName("Deve retornar comprovante digital completo")
    void deveRetornarComprovanteDigitalCompleto() {
        ModalidadeComRestricaoDeIdade modalidade =
                new ModalidadeComRestricaoDeIdade(18);

        assertThat(modalidade.getTipoComprovante())
                .isEqualTo(TipoComprovante.DIGITAL_COMPLETO);
    }

    private static Participante criarParticipante(LocalDate dataNascimento) {
        return new Participante(
                "12345",
                "Maria Silva",
                "maria@email.com",
                dataNascimento
        );
    }
}
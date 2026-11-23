package br.com.sge.sistemagestaoeventos.model;

import br.com.sge.sistemagestaoeventos.enums.StatusEvento;
import br.com.sge.sistemagestaoeventos.enums.TipoLote;
import br.com.sge.sistemagestaoeventos.exception.StatusInvalidoException;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeAberta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventoTest {

    @Test
    void cancelarEventoJaCanceladoLancaStatusInvalidoException() {
        Evento evento = criarEvento(100);
        evento.cancelar();

        assertThatThrownBy(evento::cancelar)
                .isInstanceOf(StatusInvalidoException.class)
                .hasMessage("O evento já está cancelado.");
        assertThat(evento.getStatus()).isEqualTo(StatusEvento.CANCELADO);
    }

    @Test
    void naoAvancaLoteQuandoPercentualAindaPertenceAoLoteAtual() {
        Evento evento = criarEvento(100);

        assertThat(evento.avancarLote(9)).isFalse();
        assertThat(evento.getLoteAtivo()).isEqualTo(TipoLote.PROMOCIONAL);
    }

    @Test
    void usaValorBaseZeroQuandoNaoInformado() {
        Evento evento = new Evento(new EventoDados("Evento", "Descrição", LocalDate.now().plusDays(5),
                LocalTime.of(18, 0), LocalTime.of(20, 0), "Local", 10, new ModalidadeAberta(), null));

        assertThat(evento.getValorBase()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    private static Evento criarEvento(int capacidade) {
        return new Evento(new EventoDados("Evento", "Descrição", LocalDate.now().plusDays(5),
                LocalTime.of(18, 0), LocalTime.of(20, 0), "Local", capacidade, new ModalidadeAberta(),
                new BigDecimal("100.00")));
    }
}

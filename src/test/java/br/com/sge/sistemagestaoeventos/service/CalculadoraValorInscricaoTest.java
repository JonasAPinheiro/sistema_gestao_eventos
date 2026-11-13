package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.enums.TipoLote;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.EventoDados;
import br.com.sge.sistemagestaoeventos.model.lote.LoteDois;
import br.com.sge.sistemagestaoeventos.model.lote.LotePromocional;
import br.com.sge.sistemagestaoeventos.model.lote.LoteUm;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeAberta;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeExclusivaParaAlunos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculadoraValorInscricaoTest {
    private final CalculadoraValorInscricao calculadora = new CalculadoraValorInscricao(
            List.of(new LotePromocional(), new LoteUm(), new LoteDois())
    );

    @ParameterizedTest
    @CsvSource({
            "PROMOCIONAL, 33.33, 26.66",
            "LOTE_1, 100.00, 90.00",
            "LOTE_2, 100.00, 100.00",
            "PROMOCIONAL, 0.00, 0.00"
    })
    @DisplayName("Deve calcular o valor final conforme o lote ativo")
    void deveCalcularValorFinal(TipoLote lote, BigDecimal valorBase, BigDecimal esperado) {
        Evento evento = criarEvento(new ModalidadeExclusivaParaAlunos(), valorBase, lote);

        assertThat(calculadora.calcular(evento)).isEqualByComparingTo(esperado);
    }

    @Test
    @DisplayName("Deve retornar zero para evento de modalidade aberta")
    void deveRetornarZeroParaEventoAberto() {
        Evento evento = criarEvento(new ModalidadeAberta(), new BigDecimal("100.00"), TipoLote.PROMOCIONAL);

        assertThat(calculadora.calcular(evento)).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Deve lançar exceção quando não houver estratégia para o lote")
    void deveLancarExcecaoQuandoNaoHouverEstrategiaParaOLote() {
        CalculadoraValorInscricao calculadoraSemLoteUm = new CalculadoraValorInscricao(List.of(new LotePromocional()));
        Evento evento = criarEvento(new ModalidadeExclusivaParaAlunos(), BigDecimal.TEN, TipoLote.LOTE_1);

        assertThatThrownBy(() -> calculadoraSemLoteUm.calcular(evento))
                .isInstanceOf(IllegalStateException.class);
    }

    private static Evento criarEvento(br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeEvento modalidade,
                                      BigDecimal valorBase, TipoLote lote) {
        return new Evento(
                new EventoDados(
                        "Evento de Teste",
                        "Descrição do evento",
                        LocalDate.now().plusDays(10),
                        LocalTime.of(18, 0),
                        LocalTime.of(20, 0),
                        "Centro de Eventos",
                        100,
                        modalidade,
                        valorBase,
                        lote
                )
        );
    }
}

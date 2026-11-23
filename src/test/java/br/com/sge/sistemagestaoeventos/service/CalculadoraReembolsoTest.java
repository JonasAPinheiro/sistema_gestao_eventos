
package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.EventoDados;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeAberta;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeEvento;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeExclusivaParaAlunos;
import br.com.sge.sistemagestaoeventos.model.reembolso.ReembolsoIntegral;
import br.com.sge.sistemagestaoeventos.model.reembolso.ReembolsoParcial;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculadoraReembolsoTest {

    private static final LocalDate DATA = LocalDate.of(2026, 12, 20);
    private static final LocalTime HORA = LocalTime.of(18, 0);
    private static final LocalDateTime INICIO = LocalDateTime.of(DATA, HORA);

    private final CalculadoraReembolso calculadora =
            new CalculadoraReembolso(List.of(new ReembolsoIntegral(), new ReembolsoParcial()));

    private Evento evento(ModalidadeEvento modalidade) {
        return new Evento(new EventoDados(
                "Evento", "Descrição", DATA, HORA, HORA.plusHours(2),
                "Local", 10, modalidade, new BigDecimal("100.00")
        ));
    }

    private Inscricao inscricao(Evento evento) {
        return new Inscricao(evento.getId(), "p1", new BigDecimal("80.00"));
    }

    @Test
    void reembolsoIntegralComMaisDe72h() {
        Evento evento = evento(new ModalidadeExclusivaParaAlunos());
        Inscricao inscricao = inscricao(evento);
        LocalDateTime solicitacao = INICIO.minusDays(5);

        assertEquals(
                new BigDecimal("80.00"),
                calculadora.calcular(evento, inscricao, solicitacao)
        );
    }

    @Test
    void reembolsoParcialEntre72hE24h() {
        Evento evento = evento(new ModalidadeExclusivaParaAlunos());
        Inscricao inscricao = inscricao(evento);
        LocalDateTime solicitacao = INICIO.minusHours(30);

        assertEquals(
                new BigDecimal("40.00"),
                calculadora.calcular(evento, inscricao, solicitacao)
        );
    }

    @Test
    void menosDe24hLancaRegraNegocioException() {
        Evento evento = evento(new ModalidadeExclusivaParaAlunos());
        Inscricao inscricao = inscricao(evento);
        LocalDateTime solicitacao = INICIO.minusHours(5);

        assertThrows(
                RegraNegocioException.class,
                () -> calculadora.calcular(evento, inscricao, solicitacao)
        );
    }

    @Test
    void eventoGratuitoNaoTemReembolso() {
        Evento evento = evento(new ModalidadeAberta());
        Inscricao inscricao = inscricao(evento);
        LocalDateTime solicitacao = INICIO.minusDays(10);

        assertThrows(
                RegraNegocioException.class,
                () -> calculadora.calcular(evento, inscricao, solicitacao)
        );
    }

    @Test
    void inscricaoComValorZeroNaoTemReembolso() {
        Evento evento = evento(new ModalidadeExclusivaParaAlunos());

        Inscricao inscricao = new Inscricao(
                evento.getId(),
                "p1",
                BigDecimal.ZERO
        );

        LocalDateTime solicitacao = INICIO.minusDays(10);

        assertThrows(
                RegraNegocioException.class,
                () -> calculadora.calcular(evento, inscricao, solicitacao)
        );
    }
}

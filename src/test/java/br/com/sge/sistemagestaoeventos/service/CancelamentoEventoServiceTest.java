package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.enums.StatusInscricao;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.EventoDados;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeAberta;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeExclusivaParaAlunos;
import br.com.sge.sistemagestaoeventos.repository.InscricaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelamentoEventoServiceTest {
    @Mock private EventoService eventoService;
    @Mock private InscricaoRepository inscricaoRepository;
    @Mock private ParticipanteService participanteService;
    @Mock private ReembolsoService reembolsoService;
    @Mock private NotificacaoService notificacaoService;
    @InjectMocks private CancelamentoEventoService service;

    @Test
    void confirmadasSaoReembolsadasEPendentesCanceladasSemReembolso() {
        Evento evento = new Evento(new EventoDados("Evento", "Descrição", LocalDate.now().plusDays(5),
                LocalTime.of(18, 0), LocalTime.of(20, 0), "Local", 10,
                new ModalidadeExclusivaParaAlunos(), new BigDecimal("100.00")));
        evento.cancelar();

        Inscricao confirmada = new Inscricao(evento.getId(), "p1", new BigDecimal("80.00"));
        confirmada.confirmar();
        Inscricao pendente = new Inscricao(evento.getId(), "p2", new BigDecimal("80.00"));
        Participante participante = new Participante("123", "Ana", "ana@email.com", LocalDate.of(2000, 1, 1));

        when(eventoService.cancelar(evento.getId())).thenReturn(evento);
        when(inscricaoRepository.listarPorEvento(evento.getId())).thenReturn(List.of(confirmada, pendente));
        when(participanteService.buscarPorId("p2")).thenReturn(participante);

        service.cancelar(evento.getId());

        verify(reembolsoService).reembolsarPorCancelamentoDoEvento(confirmada);
        verify(reembolsoService, never()).reembolsarPorCancelamentoDoEvento(pendente);
        verify(notificacaoService).notificarAlteracaoStatus(eq(pendente), any(Participante.class));
        assertEquals(StatusInscricao.CANCELADA, pendente.getStatus());
        assertEquals(StatusInscricao.CONFIRMADA, confirmada.getStatus());
    }

    @Test
    void inscricaoCanceladaEIgnoradaNoCancelamentoDoEvento() {
        Evento evento = new Evento(new EventoDados("Evento", "Descrição", LocalDate.now().plusDays(5),
                LocalTime.of(18, 0), LocalTime.of(20, 0), "Local", 10,
                new ModalidadeExclusivaParaAlunos(), new BigDecimal("100.00")));
        evento.cancelar();
        Inscricao cancelada = new Inscricao(evento.getId(), "p1", new BigDecimal("80.00"));
        cancelada.cancelar("Desistência");

        when(eventoService.cancelar(evento.getId())).thenReturn(evento);
        when(inscricaoRepository.listarPorEvento(evento.getId())).thenReturn(List.of(cancelada));

        service.cancelar(evento.getId());

        verify(reembolsoService, never()).reembolsarPorCancelamentoDoEvento(any());
        verify(notificacaoService, never()).notificarAlteracaoStatus(any(), any());
    }

    @Test
    void eventoGratuitoNaoReembolsaInscricaoConfirmada() {
        Evento evento = new Evento(new EventoDados("Evento", "Descrição", LocalDate.now().plusDays(5),
                LocalTime.of(18, 0), LocalTime.of(20, 0), "Local", 10,
                new ModalidadeAberta(), BigDecimal.ZERO));
        evento.cancelar();
        Inscricao confirmada = new Inscricao(evento.getId(), "p1", BigDecimal.ZERO);
        confirmada.confirmar();

        when(eventoService.cancelar(evento.getId())).thenReturn(evento);
        when(inscricaoRepository.listarPorEvento(evento.getId())).thenReturn(List.of(confirmada));

        service.cancelar(evento.getId());

        verify(reembolsoService, never()).reembolsarPorCancelamentoDoEvento(any());
    }

    @Test
    void inscricaoJaReembolsadaNaoEReembolsadaDeNovo() {
        Evento evento = new Evento(new EventoDados("Evento", "Descrição", LocalDate.now().plusDays(5),
                LocalTime.of(18, 0), LocalTime.of(20, 0), "Local", 10,
                new ModalidadeExclusivaParaAlunos(), new BigDecimal("100.00")));
        evento.cancelar();
        Inscricao confirmada = new Inscricao(evento.getId(), "p1", new BigDecimal("80.00"));
        confirmada.confirmar();
        confirmada.registrarReembolso();

        when(eventoService.cancelar(evento.getId())).thenReturn(evento);
        when(inscricaoRepository.listarPorEvento(evento.getId())).thenReturn(List.of(confirmada));

        service.cancelar(evento.getId());

        verify(reembolsoService, never()).reembolsarPorCancelamentoDoEvento(any());
    }
}
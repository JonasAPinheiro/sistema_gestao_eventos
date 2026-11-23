package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.enums.DestinoReembolso;
import br.com.sge.sistemagestaoeventos.enums.FormaPagamento;
import br.com.sge.sistemagestaoeventos.enums.MotivoReembolso;
import br.com.sge.sistemagestaoeventos.enums.StatusInscricao;
import br.com.sge.sistemagestaoeventos.exception.InscricaoNaoEncontradaException;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.exception.StatusInvalidoException;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.EventoDados;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeExclusivaParaAlunos;
import br.com.sge.sistemagestaoeventos.model.reembolso.Reembolso;
import br.com.sge.sistemagestaoeventos.repository.InscricaoRepository;
import br.com.sge.sistemagestaoeventos.repository.ReembolsoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReembolsoServiceTest {
    @Mock private EventoService eventoService;
    @Mock private ParticipanteService participanteService;
    @Mock private InscricaoRepository inscricaoRepository;
    @Mock private ReembolsoRepository reembolsoRepository;
    @Mock private CalculadoraReembolso calculadoraReembolso;
    @Mock private CarteiraService carteiraService;
    @Mock private NotificacaoService notificacaoService;
    @InjectMocks private ReembolsoService service;

    private Evento evento;
    private Participante participante;
    private Inscricao inscricao;

    @BeforeEach
    void cenario() {
        LocalDate data = LocalDate.now().plusDays(10);
        evento = new Evento(new EventoDados("Evento", "Descrição", data, LocalTime.of(18, 0), LocalTime.of(20, 0),
                "Local", 10, new ModalidadeExclusivaParaAlunos(), new BigDecimal("100.00")));
        participante = new Participante("123", "Ana", "ana@email.com", LocalDate.of(2000, 1, 1));
        inscricao = new Inscricao(evento.getId(), participante.getId(), new BigDecimal("80.00"));
        inscricao.setFormaPagamento(FormaPagamento.PIX);
        inscricao.confirmar();

        lenient().when(eventoService.buscarPorId(evento.getId())).thenReturn(evento);
        lenient().when(participanteService.buscarPorId(participante.getId())).thenReturn(participante);
        lenient().when(inscricaoRepository.buscarUltimaInscricaoPorEventoEParticipante(evento.getId(), participante.getId()))
                .thenReturn(Optional.of(inscricao));
        lenient().when(calculadoraReembolso.calcular(eq(evento), eq(inscricao), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("80.00"));
        lenient().when(reembolsoRepository.salvar(any(Reembolso.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void reembolsoParaCarteiraCreditaSaldoEMantemStatusConfirmada() {
        Reembolso reembolso = service.solicitar(evento.getId(), participante.getId(), DestinoReembolso.CARTEIRA);

        verify(carteiraService).creditar(participante.getId(), new BigDecimal("80.00"));
        verify(notificacaoService).notificarReembolso(inscricao, participante, reembolso);
        assertEquals(MotivoReembolso.SOLICITADO_PARTICIPANTE, reembolso.motivo());
        assertTrue(inscricao.isReembolsada());
        assertEquals(StatusInscricao.CONFIRMADA, inscricao.getStatus());
    }

    @Test
    void reembolsoNaFormaOriginalNaoMexeNaCarteira() {
        service.solicitar(evento.getId(), participante.getId(), DestinoReembolso.FORMA_ORIGINAL);

        verify(carteiraService, never()).creditar(any(), any());
        assertTrue(inscricao.isReembolsada());
    }

    @Test
    void segundaSolicitacaoLancaStatusInvalidoException() {
        inscricao.registrarReembolso();

        String eventoId = evento.getId();
        String participanteId = participante.getId();

        assertThrows(
                StatusInvalidoException.class,
                () -> service.solicitar(
                        eventoId,
                        participanteId,
                        DestinoReembolso.CARTEIRA
                )
        );

        verifyNoInteractions(carteiraService);
    }

    @Test
    void destinoNuloLancaRegraNegocioException() {
        String eventoId = evento.getId();
        String participanteId = participante.getId();

        assertThrows(
                RegraNegocioException.class,
                () -> service.solicitar(eventoId, participanteId, null)
        );

        verifyNoInteractions(carteiraService);
    }

    @Test
    void inscricaoInexistenteLancaInscricaoNaoEncontradaException() {
        String eventoId = evento.getId();
        String participanteId = participante.getId();

        when(inscricaoRepository.buscarUltimaInscricaoPorEventoEParticipante(
                eventoId, participanteId
        )).thenReturn(Optional.empty());

        assertThrows(
                InscricaoNaoEncontradaException.class,
                () -> service.solicitar(
                        eventoId,
                        participanteId,
                        DestinoReembolso.CARTEIRA
                )
        );

        verifyNoInteractions(carteiraService);
    }

    @Test
    void reembolsoNaFormaOriginalSemValorFinalNaoCreditaCarteira() {
        inscricao = new Inscricao(evento.getId(), participante.getId(), BigDecimal.ZERO);
        inscricao.setFormaPagamento(FormaPagamento.PIX);
        inscricao.confirmar();
        inscricao.registrarPagamentoCarteira(new BigDecimal("10.00"));
        when(inscricaoRepository.buscarUltimaInscricaoPorEventoEParticipante(evento.getId(), participante.getId()))
                .thenReturn(Optional.of(inscricao));
        when(calculadoraReembolso.calcular(eq(evento), eq(inscricao), any(LocalDateTime.class)))
                .thenReturn(BigDecimal.ZERO.setScale(2));

        service.solicitar(evento.getId(), participante.getId(), DestinoReembolso.FORMA_ORIGINAL);

        verify(carteiraService, never()).creditar(any(), any());
    }

    @Test
    void reembolsoNaFormaOriginalDevolvePartePagaComCarteira() {
        inscricao.registrarPagamentoCarteira(new BigDecimal("20.00"));

        service.solicitar(evento.getId(), participante.getId(), DestinoReembolso.FORMA_ORIGINAL);

        verify(carteiraService).creditar(participante.getId(), new BigDecimal("20.00"));
        assertTrue(inscricao.isReembolsada());
    }

    @Test
    void reembolsoPorCancelamentoDoEventoEIntegralParaCarteira() {
        Reembolso reembolso = service.reembolsarPorCancelamentoDoEvento(inscricao);

        verify(carteiraService).creditar(participante.getId(), new BigDecimal("80.00"));
        assertEquals(MotivoReembolso.EVENTO_CANCELADO, reembolso.motivo());
        assertEquals(DestinoReembolso.CARTEIRA, reembolso.destino());
    }

    @Test
    void reembolsoNaFormaOriginalComValorPagoCarteiraZeroNaoCreditaCarteira() {
        inscricao.registrarPagamentoCarteira(BigDecimal.ZERO);

        service.solicitar(
                evento.getId(),
                participante.getId(),
                DestinoReembolso.FORMA_ORIGINAL
        );

        verify(carteiraService, never()).creditar(any(), any());
        assertTrue(inscricao.isReembolsada());
    }

    @Test
    void reembolsoNaFormaOriginalComValorPagoCarteiraNuloNaoCreditaCarteira() throws Exception {
        Field campo = Inscricao.class.getDeclaredField("valorPagoCarteira");
        campo.setAccessible(true);
        campo.set(inscricao, null);

        service.solicitar(
                evento.getId(),
                participante.getId(),
                DestinoReembolso.FORMA_ORIGINAL
        );

        verify(carteiraService, never()).creditar(any(), any());
        assertTrue(inscricao.isReembolsada());
    }


}
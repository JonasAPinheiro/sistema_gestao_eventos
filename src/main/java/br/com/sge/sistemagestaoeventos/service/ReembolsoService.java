package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.enums.DestinoReembolso;
import br.com.sge.sistemagestaoeventos.enums.MotivoReembolso;
import br.com.sge.sistemagestaoeventos.exception.InscricaoNaoEncontradaException;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.model.reembolso.Reembolso;
import br.com.sge.sistemagestaoeventos.repository.InscricaoRepository;
import br.com.sge.sistemagestaoeventos.repository.ReembolsoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class ReembolsoService {
    private static final Logger log = LoggerFactory.getLogger(ReembolsoService.class);
    private static final String MOTIVO_SOLICITACAO = "Reembolso solicitado pelo participante";

    private final EventoService eventoService;
    private final ParticipanteService participanteService;
    private final InscricaoRepository inscricaoRepository;
    private final ReembolsoRepository reembolsoRepository;
    private final CalculadoraReembolso calculadoraReembolso;
    private final CarteiraService carteiraService;
    private final NotificacaoService notificacaoService;

    public ReembolsoService(EventoService eventoService, ParticipanteService participanteService,
                            InscricaoRepository inscricaoRepository, ReembolsoRepository reembolsoRepository,
                            CalculadoraReembolso calculadoraReembolso, CarteiraService carteiraService,
                            NotificacaoService notificacaoService) {
        this.eventoService = eventoService;
        this.participanteService = participanteService;
        this.inscricaoRepository = inscricaoRepository;
        this.reembolsoRepository = reembolsoRepository;
        this.calculadoraReembolso = calculadoraReembolso;
        this.carteiraService = carteiraService;
        this.notificacaoService = notificacaoService;
    }

    /** Fluxo do participante: ele escolhe o destino. */
    public Reembolso solicitar(String eventoId, String participanteId, DestinoReembolso destino) {
        if (destino == null) {
            throw new RegraNegocioException("O destino do reembolso é obrigatório.");
        }

        Evento evento = eventoService.buscarPorId(eventoId);
        Participante participante = participanteService.buscarPorId(participanteId);
        Inscricao inscricao = inscricaoRepository
                .buscarUltimaInscricaoPorEventoEParticipante(eventoId, participanteId)
                .orElseThrow(() -> new InscricaoNaoEncontradaException(eventoId, participanteId));

        inscricao.validarReembolsavel();

        LocalDateTime inicioEvento = LocalDateTime.of(evento.getData(), evento.getHoraInicio());
        LocalDateTime agora = LocalDateTime.now(Clock.systemDefaultZone());

        evento.getModalidade().validarCancelamento(inicioEvento, agora, MOTIVO_SOLICITACAO);
        BigDecimal valor = calculadoraReembolso.calcular(evento, inscricao, agora);

        Reembolso reembolso = processar(inscricao, valor, destino, MotivoReembolso.SOLICITADO_PARTICIPANTE);
        notificacaoService.notificarReembolso(inscricao, participante, reembolso);
        return reembolso;
    }

    /** Fluxo do cancelamento do evento: sempre integral e para a carteira (voucher). */
    public Reembolso reembolsarPorCancelamentoDoEvento(Inscricao inscricao) {
        Participante participante = participanteService.buscarPorId(inscricao.getParticipanteId());
        BigDecimal valor = inscricao.getValorFinal().setScale(2, RoundingMode.HALF_UP);

        Reembolso reembolso = processar(inscricao, valor, DestinoReembolso.CARTEIRA, MotivoReembolso.EVENTO_CANCELADO);
        notificacaoService.notificarReembolso(inscricao, participante, reembolso);
        return reembolso;
    }

    private Reembolso processar(Inscricao inscricao, BigDecimal valor, DestinoReembolso destino, MotivoReembolso motivo) {
        // A parte paga com a carteira sempre volta para a carteira.
        BigDecimal paraCarteira = destino == DestinoReembolso.CARTEIRA ? valor : parteDaCarteira(inscricao, valor);
        BigDecimal paraFormaOriginal = valor.subtract(paraCarteira);

        if (paraCarteira.signum() > 0) {
            carteiraService.creditar(inscricao.getParticipanteId(), paraCarteira);
        }
        if (paraFormaOriginal.signum() > 0) {
            log.info("Estorno simulado de R$ {} via {} para a inscrição {}",
                    paraFormaOriginal, inscricao.getFormaPagamento(), inscricao.getId());
        }

        inscricao.registrarReembolso();
        inscricaoRepository.salvar(inscricao);

        return reembolsoRepository.salvar(Reembolso.criar(
                inscricao.getId(), inscricao.getParticipanteId(), valor, destino, motivo));
    }

    private BigDecimal parteDaCarteira(Inscricao inscricao, BigDecimal valorReembolso) {
        BigDecimal pagoCarteira = inscricao.getValorPagoCarteira();
        if (pagoCarteira == null || pagoCarteira.signum() <= 0 || inscricao.getValorFinal().signum() <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return valorReembolso.multiply(pagoCarteira)
                .divide(inscricao.getValorFinal(), 2, RoundingMode.HALF_UP);
    }
}
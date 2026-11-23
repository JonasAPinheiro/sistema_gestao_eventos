package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.enums.StatusInscricao;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.repository.InscricaoRepository;
import org.springframework.stereotype.Service;

@Service
public class CancelamentoEventoService {
    private static final String MOTIVO = "Evento cancelado";

    private final EventoService eventoService;
    private final InscricaoRepository inscricaoRepository;
    private final ParticipanteService participanteService;
    private final ReembolsoService reembolsoService;
    private final NotificacaoService notificacaoService;

    public CancelamentoEventoService(EventoService eventoService, InscricaoRepository inscricaoRepository,
                                     ParticipanteService participanteService, ReembolsoService reembolsoService,
                                     NotificacaoService notificacaoService) {
        this.eventoService = eventoService;
        this.inscricaoRepository = inscricaoRepository;
        this.participanteService = participanteService;
        this.reembolsoService = reembolsoService;
        this.notificacaoService = notificacaoService;
    }

    public Evento cancelar(String eventoId) {
        Evento evento = eventoService.cancelar(eventoId);
        boolean eventoPago = !evento.getModalidade().isGratuita();

        for (Inscricao inscricao : inscricaoRepository.listarPorEvento(eventoId)) {
            if (inscricao.getStatus() == StatusInscricao.PENDENTE) {
                // Cancela direto, sem a validação da modalidade e sem reembolso (nada foi pago).
                inscricao.cancelar(MOTIVO);
                inscricaoRepository.salvar(inscricao);
                notificacaoService.notificarAlteracaoStatus(
                        inscricao, participanteService.buscarPorId(inscricao.getParticipanteId()));
            } else if (inscricao.getStatus() == StatusInscricao.CONFIRMADA
                    && eventoPago && !inscricao.isReembolsada()) {
                reembolsoService.reembolsarPorCancelamentoDoEvento(inscricao);
            }
        }
        return evento;
    }
}
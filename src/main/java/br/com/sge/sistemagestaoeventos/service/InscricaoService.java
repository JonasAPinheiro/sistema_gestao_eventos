package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.enums.StatusEvento;
import br.com.sge.sistemagestaoeventos.exception.InscricaoNaoEncontradaException;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.model.modalidade.ValidadorElegibilidade;
import br.com.sge.sistemagestaoeventos.repository.InscricaoRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InscricaoService {

    private final InscricaoRepository inscricaoRepository;
    private final EventoService eventoService;
    private final ParticipanteService participanteService;

    public InscricaoService(InscricaoRepository inscricaoRepository, EventoService eventoService, ParticipanteService participanteService) {
        this.inscricaoRepository = inscricaoRepository;
        this.eventoService = eventoService;
        this.participanteService = participanteService;
    }

    public Inscricao inscrever(String eventoId, String participanteId) {
        Evento evento = eventoService.buscarPorId(eventoId);
        Participante participante = participanteService.buscarPorId(participanteId);

        if (evento.getStatus() == StatusEvento.CANCELADO) {
            throw new RegraNegocioException("Não é possível realizar inscrição em um evento cancelado.");
        }

        LocalDateTime inicioEvento = LocalDateTime.of(evento.getData(), evento.getHoraInicio());
        if (!inicioEvento.isAfter(LocalDateTime.now(Clock.systemDefaultZone()))) {
            throw new RegraNegocioException("Não é possível realizar inscrição após o início do evento.");
        }

        inscricaoRepository.buscarInscricaoAtivaPorEventoEParticipante(eventoId, participanteId)
                .ifPresent(i -> {
                    throw new RegraNegocioException("O participante já possui uma inscrição ativa neste evento.");
                });

        long vagasDisponiveis = evento.getCapacidadeMaxima() - inscricaoRepository.contarConfirmadasPorEvento(eventoId);

        if (vagasDisponiveis <= 0) {
            throw new RegraNegocioException("Não há vagas disponíveis para este evento.");
        }

        if (evento.getModalidade() instanceof ValidadorElegibilidade validador) {
            validador.validarElegibilidade(participante);
        }

        return inscricaoRepository.salvar(new Inscricao(eventoId, participanteId));
    }

    public void cancelar(String eventoId, String participanteId, String motivo) {
        Evento evento = eventoService.buscarPorId(eventoId);
        participanteService.buscarPorId(participanteId);

        Inscricao inscricao = inscricaoRepository.buscarInscricaoAtivaPorEventoEParticipante(eventoId, participanteId)
                .orElseThrow(() -> new InscricaoNaoEncontradaException(eventoId, participanteId));

        LocalDateTime inicioEvento = LocalDateTime.of(evento.getData(), evento.getHoraInicio());

        evento.getModalidade().validarCancelamento(inicioEvento, LocalDateTime.now(Clock.systemDefaultZone()), motivo);

        inscricao.cancelar(motivo);
        inscricaoRepository.salvar(inscricao);
    }

    public List<Inscricao> listarPorEvento(String eventoId) {
        eventoService.buscarPorId(eventoId);
        return inscricaoRepository.listarPorEvento(eventoId);
    }

    public List<Inscricao> listarPorParticipante(String participanteId) {
        participanteService.buscarPorId(participanteId);
        return inscricaoRepository.listarPorParticipante(participanteId);
    }

    public Inscricao consultar(String eventoId, String participanteId) {
        eventoService.buscarPorId(eventoId);
        participanteService.buscarPorId(participanteId);
        return inscricaoRepository.buscarInscricaoAtivaPorEventoEParticipante(eventoId, participanteId)
                .orElseThrow(() -> new InscricaoNaoEncontradaException(eventoId, participanteId));
    }

    public long contarConfirmadas(String eventoId) {
        return inscricaoRepository.contarConfirmadasPorEvento(eventoId);
    }
}
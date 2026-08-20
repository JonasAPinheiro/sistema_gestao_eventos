package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.enums.StatusEvento;
import br.com.sge.sistemagestaoeventos.exception.ConflitoException;
import br.com.sge.sistemagestaoeventos.exception.InscricaoNaoEncontradaException;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.repository.InscricaoRepository;
import org.springframework.stereotype.Service;

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
        participanteService.buscarPorId(participanteId);

        if (evento.getStatus() == StatusEvento.CANCELADO) {
            throw new RegraNegocioException("Não é possível realizar inscrição em um evento cancelado.");
        }

        LocalDateTime inicioEvento = LocalDateTime.of(evento.getData(), evento.getHoraInicio());
        if (!inicioEvento.isAfter(LocalDateTime.now())) {
            throw new RegraNegocioException("Não é possível realizar inscrição após o início do evento.");
        }

        inscricaoRepository.buscarInscricaoAtivaPorEventoEParticipante(eventoId, participanteId)
                .ifPresent(i -> {
                    throw new ConflitoException("O participante já possui uma inscrição ativa neste evento.");
                });

        if (inscricaoRepository.contarConfirmadasPorEvento(eventoId) >= evento.getCapacidadeMaxima()) {
            throw new RegraNegocioException("O evento atingiu sua capacidade máxima.");
        }

        return inscricaoRepository.salvar(new Inscricao(eventoId, participanteId));
    }

    public void cancelar(String eventoId, String participanteId) {
        eventoService.buscarPorId(eventoId);
        participanteService.buscarPorId(participanteId);

        Inscricao inscricao = inscricaoRepository.buscarInscricaoAtivaPorEventoEParticipante(eventoId, participanteId)
                .orElseThrow(() -> new InscricaoNaoEncontradaException(eventoId, participanteId));

        inscricao.cancelar();
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
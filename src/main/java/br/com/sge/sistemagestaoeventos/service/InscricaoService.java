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
    private final CalculadoraValorInscricao calculadoraValor;
    private final NotificacaoService notificacaoService;

    public InscricaoService(InscricaoRepository inscricaoRepository, EventoService eventoService,
                            ParticipanteService participanteService, CalculadoraValorInscricao calculadoraValor,
                            NotificacaoService notificacaoService) {
        this.inscricaoRepository = inscricaoRepository;
        this.eventoService = eventoService;
        this.participanteService = participanteService;
        this.calculadoraValor = calculadoraValor;
        this.notificacaoService = notificacaoService;
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

        if (evento.getModalidade() instanceof ValidadorElegibilidade validador) {
            validador.validarElegibilidade(participante);
        }

        return inscricaoRepository.salvar(
                new Inscricao(eventoId, participanteId, calculadoraValor.calcular(evento)));
    }

    public Inscricao confirmar(String eventoId, String participanteId) {
        Evento evento = eventoService.buscarPorId(eventoId);
        Participante participante = participanteService.buscarPorId(participanteId);
        Inscricao inscricao = inscricaoRepository.buscarUltimaInscricaoPorEventoEParticipante(eventoId, participanteId)
                .orElseThrow(() -> new InscricaoNaoEncontradaException(eventoId, participanteId));
        inscricao.validarPendente();

        if (evento.getStatus() == StatusEvento.CANCELADO) {
            throw new RegraNegocioException("Não é possível confirmar inscrição em um evento cancelado.");
        }
        LocalDateTime inicioEvento = LocalDateTime.of(evento.getData(), evento.getHoraInicio());
        if (!inicioEvento.isAfter(LocalDateTime.now(Clock.systemDefaultZone()))) {
            throw new RegraNegocioException("Não é possível confirmar inscrição após o início do evento.");
        }

        long confirmadas = inscricaoRepository.contarConfirmadasPorEvento(eventoId);
        if (confirmadas >= evento.getCapacidadeMaxima()) {
            throw new RegraNegocioException("Não há vagas disponíveis para confirmar a inscrição.");
        }

        inscricao.confirmar();
        inscricaoRepository.salvar(inscricao);
        eventoService.avancarLoteSeNecessario(evento, confirmadas + 1);
        notificacaoService.notificarAlteracaoStatus(inscricao, participante);
        return inscricao;
    }

    public void cancelar(String eventoId, String participanteId, String motivo) {
        Evento evento = eventoService.buscarPorId(eventoId);
        Participante participante = participanteService.buscarPorId(participanteId);

        Inscricao inscricao = inscricaoRepository.buscarUltimaInscricaoPorEventoEParticipante(eventoId, participanteId)
                .orElseThrow(() -> new InscricaoNaoEncontradaException(eventoId, participanteId));
        inscricao.validarPendente();

        LocalDateTime inicioEvento = LocalDateTime.of(evento.getData(), evento.getHoraInicio());

        evento.getModalidade().validarCancelamento(inicioEvento, LocalDateTime.now(Clock.systemDefaultZone()), motivo);

        inscricao.cancelar(motivo);
        inscricaoRepository.salvar(inscricao);
        notificacaoService.notificarAlteracaoStatus(inscricao, participante);
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

package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.exception.EventoNaoEncontradoException;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public Evento buscarPorId(String id) {
        return eventoRepository.buscarPorId(id)
                .orElseThrow(() -> new EventoNaoEncontradoException(id));
    }
    public List<Evento> listarTodos() {
        return eventoRepository.listarTodos();
    }

    public Evento cadastrar(Evento evento) {
        validarDadosDoEvento(evento);
        return eventoRepository.salvar(evento);
    }

    public Evento atualizar(String id, Evento dadosAtualizados) {
        Evento eventoExistente = buscarPorId(id);
        validarDadosDoEvento(dadosAtualizados);

        eventoExistente.setTitulo(dadosAtualizados.getTitulo());
        eventoExistente.setDescricao(dadosAtualizados.getDescricao());
        eventoExistente.setData(dadosAtualizados.getData());
        eventoExistente.setHorarioInicio(dadosAtualizados.getHorarioInicio());
        eventoExistente.setHorarioTermino(dadosAtualizados.getHorarioTermino());
        eventoExistente.setLocal(dadosAtualizados.getLocal());
        eventoExistente.setCapacidadeMaxima(dadosAtualizados.getCapacidadeMaxima());

        return eventoRepository.salvar(eventoExistente);
    }

    public void cancelar(String id) {
        Evento evento = buscarPorId(id);
        evento.cancelar();
        eventoRepository.salvar(evento);
    }

    private void validarDadosDoEvento(Evento evento) {
        if (evento.getTitulo() == null || evento.getTitulo().isBlank()) {
            throw new RegraNegocioException("O título do evento é obrigatório.");
        }

        if (evento.getDescricao() == null || evento.getDescricao().isBlank()) {
            throw new RegraNegocioException("A descrição do evento é obrigatória.");
        }

        if (evento.getData() == null || evento.getData().isBefore(LocalDate.now())) {
            throw new RegraNegocioException("A data do evento não pode ser anterior à data atual.");
        }

        if (evento.getHorarioInicio() == null || evento.getHorarioTermino() == null
                || !evento.getHorarioTermino().isAfter(evento.getHorarioInicio())) {
            throw new RegraNegocioException("O horário de término deve ser posterior ao horário de início.");
        }

        if (evento.getCapacidadeMaxima() <= 0) {
            throw new RegraNegocioException("A capacidade máxima deve ser um número inteiro maior que zero.");
        }
    }
}
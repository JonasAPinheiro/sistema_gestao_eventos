package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.dto.EventoRequestDTO;
import br.com.sge.sistemagestaoeventos.exception.EventoNaoEncontradoException;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeEvento;
import br.com.sge.sistemagestaoeventos.model.modalidade.factory.ModalidadeEventoFactory;
import br.com.sge.sistemagestaoeventos.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final ModalidadeEventoFactory modalidadeEventoFactory;

    public EventoService(
            EventoRepository eventoRepository,
            ModalidadeEventoFactory modalidadeEventoFactory
    ) {
        this.eventoRepository = eventoRepository;
        this.modalidadeEventoFactory = modalidadeEventoFactory;
    }

    public Evento buscarPorId(String id) {
        return eventoRepository.buscarPorId(id)
                .orElseThrow(() -> new EventoNaoEncontradoException(id));
    }

    public List<Evento> listarTodos() {
        return eventoRepository.listarTodos();
    }

    public Evento cadastrar(EventoRequestDTO dto) {

        ModalidadeEvento modalidade = modalidadeEventoFactory.criar(
                dto.tipoModalidade(),
                dto.idadeMinima()
        );

        Evento evento = dto.toEvento(modalidade);

        validarDadosDoEvento(evento);

        return eventoRepository.salvar(evento);
    }

    public Evento atualizar(String id, EventoRequestDTO dto) {

        Evento eventoExistente = buscarPorId(id);

        eventoExistente.setTitulo(dto.titulo());
        eventoExistente.setDescricao(dto.descricao());
        eventoExistente.setData(dto.data());
        eventoExistente.setHoraInicio(dto.horaInicio());
        eventoExistente.setHoraFim(dto.horaFim());
        eventoExistente.setLocal(dto.local());
        eventoExistente.setCapacidadeMaxima(dto.capacidadeMaxima());

        validarDadosDoEvento(eventoExistente);

        return eventoRepository.salvar(eventoExistente);
    }

    public Evento cancelar(String id) {
        Evento evento = buscarPorId(id);
        evento.cancelar();
        return eventoRepository.salvar(evento);
    }

    private void validarDadosDoEvento(Evento evento) {

        if (evento.getTitulo() == null || evento.getTitulo().isBlank()) {
            throw new RegraNegocioException("O título do evento é obrigatório.");
        }

        if (evento.getDescricao() == null || evento.getDescricao().isBlank()) {
            throw new RegraNegocioException("A descrição do evento é obrigatória.");
        }

        if (evento.getData() == null ||
                evento.getData().isBefore(LocalDate.now(Clock.systemDefaultZone()))) {

            throw new RegraNegocioException(
                    "A data do evento não pode ser anterior à data atual."
            );
        }

        if (evento.getHoraInicio() == null ||
                evento.getHoraFim() == null ||
                !evento.getHoraFim().isAfter(evento.getHoraInicio())) {

            throw new RegraNegocioException(
                    "O horário de término deve ser posterior ao horário de início."
            );
        }

        if (evento.getCapacidadeMaxima() <= 0) {
            throw new RegraNegocioException(
                    "A capacidade máxima deve ser um número inteiro maior que zero."
            );
        }
    }
}
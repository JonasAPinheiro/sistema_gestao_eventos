package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeAberta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EventoRepositoryMemoriaTest {

    private EventoRepositoryMemoria repository;

    @BeforeEach
    void setUp() {
        repository = new EventoRepositoryMemoria();
    }

    private Evento criarEvento() {
        return new Evento(
                "Java Day",
                "Evento sobre Java",
                LocalDate.now(),
                LocalTime.of(19, 0),
                LocalTime.of(22, 0),
                "Auditório",
                100,
                new ModalidadeAberta()
        );
    }

    @Test
    void deveSalvarEvento() {
        Evento evento = criarEvento();

        Evento salvo = repository.salvar(evento);

        assertEquals(evento, salvo);
    }

    @Test
    void deveBuscarEventoPorId() {
        Evento evento = criarEvento();
        repository.salvar(evento);

        Optional<Evento> resultado = repository.buscarPorId(evento.getId());

        assertTrue(resultado.isPresent());
        assertEquals(evento, resultado.get());
    }

    @Test
    void deveRetornarVazioQuandoNaoEncontrar() {
        Optional<Evento> resultado = repository.buscarPorId("id-inexistente");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveListarTodos() {
        Evento evento1 = criarEvento();
        Evento evento2 = criarEvento();

        repository.salvar(evento1);
        repository.salvar(evento2);

        List<Evento> eventos = repository.listarTodos();

        assertEquals(2, eventos.size());
        assertTrue(eventos.contains(evento1));
        assertTrue(eventos.contains(evento2));
    }
}
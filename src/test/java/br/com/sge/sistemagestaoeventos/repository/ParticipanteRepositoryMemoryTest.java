package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.model.Participante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ParticipanteRepositoryMemoriaTest {

    private ParticipanteRepositoryMemoria repository;

    @BeforeEach
    void setUp() {
        repository = new ParticipanteRepositoryMemoria();
    }

    private Participante criarParticipante(String matricula, String nome, String email) {
        return new Participante(
                matricula,
                nome,
                email,
                LocalDate.of(2000, 1, 1)
        );
    }

    @Test
    void deveSalvarParticipante() {
        Participante participante = criarParticipante(
                "2023001",
                "Jonas",
                "jonas@email.com"
        );

        Participante salvo = repository.salvar(participante);

        assertEquals(participante, salvo);
    }

    @Test
    void deveBuscarParticipantePorId() {
        Participante participante = criarParticipante(
                "2023001",
                "Jonas",
                "jonas@email.com"
        );

        repository.salvar(participante);

        Optional<Participante> resultado = repository.buscarPorId(participante.getId());

        assertTrue(resultado.isPresent());
        assertEquals(participante, resultado.get());
    }

    @Test
    void deveRetornarVazioQuandoIdNaoExistir() {
        Optional<Participante> resultado = repository.buscarPorId("id-inexistente");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveBuscarParticipantePorEmail() {
        Participante participante = criarParticipante(
                "2023001",
                "Jonas",
                "jonas@email.com"
        );

        repository.salvar(participante);

        Optional<Participante> resultado =
                repository.buscarPorEmail("jonas@email.com");

        assertTrue(resultado.isPresent());
        assertEquals(participante, resultado.get());
    }

    @Test
    void deveBuscarEmailIgnorandoMaiusculasEMinusculas() {
        Participante participante = criarParticipante(
                "2023001",
                "Jonas",
                "jonas@email.com"
        );

        repository.salvar(participante);

        Optional<Participante> resultado =
                repository.buscarPorEmail("JONAS@EMAIL.COM");

        assertTrue(resultado.isPresent());
    }

    @Test
    void deveRetornarVazioQuandoEmailForNulo() {
        Optional<Participante> resultado = repository.buscarPorEmail(null);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveRetornarVazioQuandoEmailNaoExistir() {
        Optional<Participante> resultado =
                repository.buscarPorEmail("naoexiste@email.com");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveListarTodosOsParticipantes() {
        Participante participante1 = criarParticipante(
                "2023001",
                "Jonas",
                "jonas@email.com"
        );

        Participante participante2 = criarParticipante(
                "2023002",
                "Maria",
                "maria@email.com"
        );

        repository.salvar(participante1);
        repository.salvar(participante2);

        List<Participante> participantes = repository.listarTodos();

        assertEquals(2, participantes.size());
        assertTrue(participantes.contains(participante1));
        assertTrue(participantes.contains(participante2));
    }
}
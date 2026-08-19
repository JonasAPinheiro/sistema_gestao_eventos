package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.model.Participante;

import java.util.List;
import java.util.Optional;

public interface ParticipanteRepository {
    Participante salvar(Participante participante);
    Optional<Participante> buscarPorId(String id);
    Optional<Participante> buscarPorEmail(String email);
    List<Participante> listarTodos();
    boolean existePorId(String id);
}

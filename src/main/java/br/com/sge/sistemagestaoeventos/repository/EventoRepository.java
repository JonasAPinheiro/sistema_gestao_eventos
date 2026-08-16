package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.model.Evento;

import java.util.List;
import java.util.Optional;

public interface EventoRepository {
    Evento salvar (Evento evento);
    Optional<Evento> buscarPorId(String id);
    List<Evento> listarTodos();
    boolean existePorId(String id);
}

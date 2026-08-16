package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.model.Evento;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EventoRepositoryMemoria implements EventoRepository{
    private final Map<String, Evento> dados = new ConcurrentHashMap<>();

    @Override
    public Optional<Evento> buscarPorId(String id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Evento> listarTodos() {
        return List.copyOf(dados.values());
    }

    @Override
    public boolean existePorId(String id) {
        return dados.containsKey(id);
    }

    @Override
    public Evento salvar(Evento evento) {
        dados.put(evento.getId(), evento);
        return evento;
    }
}

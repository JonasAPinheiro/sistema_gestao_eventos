package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.model.Participante;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ParticipanteRepositoryMemoria implements ParticipanteRepository {
    private final Map<String, Participante> dados = new ConcurrentHashMap<>();

    @Override
    public Participante salvar(Participante participante) {
        dados.put(participante.getId(), participante);
        return participante;
    }

    @Override
    public Optional<Participante> buscarPorId(String id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public Optional<Participante> buscarPorEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return dados.values().stream()
                .filter(p -> email.equalsIgnoreCase(p.getEmail()))
                .findFirst();
    }

    @Override
    public List<Participante> listarTodos() {
        return List.copyOf(dados.values());
    }

    @Override
    public boolean existePorId(String id) {
        return dados.containsKey(id);
    }
}

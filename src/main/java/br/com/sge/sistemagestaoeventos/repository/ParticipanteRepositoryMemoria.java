package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.model.Participante;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ParticipanteRepositoryMemoria implements ParticipanteRepository {
    private final Map<String, Participante> participantes = new ConcurrentHashMap<>();

    @Override
    public Participante salvar(Participante participante) {
        participantes.put(participante.getId(), participante);
        return participante;
    }

    @Override
    public Optional<Participante> buscarPorId(String id) {
        return Optional.ofNullable(participantes.get(id));
    }

    @Override
    public Optional<Participante> buscarPorEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }

        return participantes.values().stream()
                .filter(participante -> email.equalsIgnoreCase(participante.getEmail()))
                .findFirst();
    }

    @Override
    public List<Participante> listarTodos() {
        return List.copyOf(participantes.values());
    }
}

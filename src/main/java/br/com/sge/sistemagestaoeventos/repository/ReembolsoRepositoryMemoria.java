package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.model.reembolso.Reembolso;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ReembolsoRepositoryMemoria implements ReembolsoRepository {
    private final Map<String, Reembolso> reembolsosPorId = new ConcurrentHashMap<>();

    @Override
    public Reembolso salvar(Reembolso reembolso) {
        reembolsosPorId.put(reembolso.id(), reembolso);
        return reembolso;
    }

    @Override
    public Optional<Reembolso> buscarPorInscricaoId(String inscricaoId) {
        return reembolsosPorId.values().stream()
                .filter(r -> r.inscricaoId().equals(inscricaoId))
                .findFirst();
    }

    @Override
    public List<Reembolso> listarPorParticipante(String participanteId) {
        return reembolsosPorId.values().stream()
                .filter(r -> r.participanteId().equals(participanteId))
                .toList();
    }
}
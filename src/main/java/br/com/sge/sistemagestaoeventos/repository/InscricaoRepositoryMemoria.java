package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.enums.StatusInscricao;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InscricaoRepositoryMemoria implements InscricaoRepository {
    private final Map<String, Inscricao> inscricoesPorId = new ConcurrentHashMap<>();

    @Override
    public Inscricao salvar(Inscricao inscricao) {
        inscricoesPorId.put(inscricao.getId(), inscricao);
        return inscricao;
    }

    @Override
    public Optional<Inscricao> buscarInscricaoAtivaPorEventoEParticipante(String eventoId, String participanteId) {
        return inscricoesPorId.values().stream()
                .filter(inscricao -> inscricao.getEventoId().equals(eventoId))
                .filter(inscricao -> inscricao.getParticipanteId().equals(participanteId))
                .filter(inscricao -> inscricao.getStatus() == StatusInscricao.CONFIRMADA)
                .findFirst();
    }

    @Override
    public List<Inscricao> listarPorEvento(String eventoId) {
        return inscricoesPorId.values().stream()
                .filter(inscricao -> inscricao.getEventoId().equals(eventoId))
                .toList();
    }

    @Override
    public List<Inscricao> listarPorParticipante(String participanteId) {
        return inscricoesPorId.values().stream()
                .filter(inscricao -> inscricao.getParticipanteId().equals(participanteId))
                .toList();
    }

    @Override
    public long contarConfirmadasPorEvento(String eventoId) {
        return inscricoesPorId.values().stream()
                .filter(inscricao -> inscricao.getEventoId().equals(eventoId))
                .filter(inscricao -> inscricao.getStatus() == StatusInscricao.CONFIRMADA)
                .count();
    }
}

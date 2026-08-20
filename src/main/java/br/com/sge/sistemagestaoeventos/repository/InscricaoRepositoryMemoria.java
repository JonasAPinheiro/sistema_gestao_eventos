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
    private final Map<String, Inscricao> dados = new ConcurrentHashMap<>();

    @Override
    public Inscricao salvar(Inscricao inscricao) {
        dados.put(inscricao.getId(), inscricao);
        return inscricao;
    }

    @Override
    public Optional<Inscricao> buscarInscricaoAtivaPorEventoEParticipante(String eventoId, String participanteId) {
        return dados.values().stream()
                .filter(i -> i.getEventoId().equals(eventoId))
                .filter(i -> i.getParticipanteId().equals(participanteId))
                .filter(i -> i.getStatus() == StatusInscricao.CONFIRMADA)
                .findFirst();
    }

    @Override
    public List<Inscricao> listarPorEvento(String eventoId) {
        return dados.values().stream()
                .filter(i -> i.getEventoId().equals(eventoId))
                .toList();
    }

    @Override
    public List<Inscricao> listarPorParticipante(String participanteId) {
        return dados.values().stream()
                .filter(i -> i.getParticipanteId().equals(participanteId))
                .toList();
    }

    @Override
    public long contarConfirmadasPorEvento(String eventoId) {
        return dados.values().stream()
                .filter(i -> i.getEventoId().equals(eventoId))
                .filter(i -> i.getStatus() == StatusInscricao.CONFIRMADA)
                .count();
    }
}
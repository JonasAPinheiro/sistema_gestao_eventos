package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.model.notificacao.Notificacao;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class NotificacaoRepositoryMemoria implements NotificacaoRepository {
    private final Map<String, Notificacao> notificacoesPorId = new ConcurrentHashMap<>();

    @Override
    public Notificacao salvar(Notificacao notificacao) {
        notificacoesPorId.put(notificacao.id(), notificacao);
        return notificacao;
    }

    @Override
    public List<Notificacao> listarTodas() {
        return notificacoesPorId.values().stream()
                .sorted(Comparator.comparing(Notificacao::criadaEm).reversed())
                .toList();
    }
}

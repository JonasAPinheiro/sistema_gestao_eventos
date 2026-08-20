package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.model.Inscricao;

import java.util.List;
import java.util.Optional;

public interface InscricaoRepository {
    Inscricao salvar(Inscricao inscricao);
    Optional<Inscricao> buscarInscricaoAtivaPorEventoEParticipante(String eventoId, String participanteId);
    List<Inscricao> listarPorEvento(String eventoId);
    List<Inscricao> listarPorParticipante(String participanteId);
    long contarConfirmadasPorEvento(String eventoId);
}
package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.model.reembolso.Reembolso;

import java.util.List;
import java.util.Optional;

public interface ReembolsoRepository {
    Reembolso salvar(Reembolso reembolso);

    Optional<Reembolso> buscarPorInscricaoId(String inscricaoId);

    List<Reembolso> listarPorParticipante(String participanteId);
}
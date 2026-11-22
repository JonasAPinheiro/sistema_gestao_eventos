package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.model.carteira.Carteira;

import java.util.Optional;

public interface CarteiraRepository {
    Carteira salvar(Carteira carteira);

    Optional<Carteira> buscarPorParticipanteId(String participanteId);
}
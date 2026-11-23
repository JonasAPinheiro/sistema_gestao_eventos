package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.model.carteira.Carteira;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CarteiraRepositoryMemoria implements CarteiraRepository {
    private final Map<String, Carteira> carteirasPorParticipante = new ConcurrentHashMap<>();

    @Override
    public Carteira salvar(Carteira carteira) {
        carteirasPorParticipante.put(carteira.getParticipanteId(), carteira);
        return carteira;
    }

    @Override
    public Optional<Carteira> buscarPorParticipanteId(String participanteId) {
        return Optional.ofNullable(carteirasPorParticipante.get(participanteId));
    }
}
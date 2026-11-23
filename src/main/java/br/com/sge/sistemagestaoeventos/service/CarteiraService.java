package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.model.carteira.Carteira;
import br.com.sge.sistemagestaoeventos.repository.CarteiraRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CarteiraService {
    private final CarteiraRepository carteiraRepository;
    private final ParticipanteService participanteService;

    public CarteiraService(CarteiraRepository carteiraRepository, ParticipanteService participanteService) {
        this.carteiraRepository = carteiraRepository;
        this.participanteService = participanteService;
    }

    public Carteira consultar(String participanteId) {
        participanteService.buscarPorId(participanteId);
        return obterOuCriar(participanteId);
    }

    public void creditar(String participanteId, BigDecimal valor) {
        Carteira carteira = obterOuCriar(participanteId);
        carteira.creditar(valor);
        carteiraRepository.salvar(carteira);
    }

    public void debitar(String participanteId, BigDecimal valor) {
        Carteira carteira = obterOuCriar(participanteId);
        carteira.debitar(valor);
        carteiraRepository.salvar(carteira);
    }

    private Carteira obterOuCriar(String participanteId) {
        return carteiraRepository.buscarPorParticipanteId(participanteId)
                .orElseGet(() -> carteiraRepository.salvar(new Carteira(participanteId)));
    }
}
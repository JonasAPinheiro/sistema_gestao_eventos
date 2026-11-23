package br.com.sge.sistemagestaoeventos.dto;

import br.com.sge.sistemagestaoeventos.model.carteira.Carteira;

import java.math.BigDecimal;

public record CarteiraResponseDTO(String participanteId, BigDecimal saldo) {
    public static CarteiraResponseDTO from(Carteira carteira) {
        return new CarteiraResponseDTO(carteira.getParticipanteId(), carteira.getSaldo());
    }
}
package br.com.sge.sistemagestaoeventos.dto;

import br.com.sge.sistemagestaoeventos.enums.FormaPagamento;

public record InscricaoRequestDTO(
        String participanteId,
        FormaPagamento formaPagamento
) {
    public InscricaoRequestDTO(String participanteId) {
        this(participanteId, null);
    }
}
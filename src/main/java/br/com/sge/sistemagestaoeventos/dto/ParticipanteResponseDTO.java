package br.com.sge.sistemagestaoeventos.dto;

import br.com.sge.sistemagestaoeventos.model.Participante;

import java.time.LocalDateTime;

public record ParticipanteResponseDTO(
        String id,
        String nome,
        String email,
        LocalDateTime criadoEm
) {
    public static ParticipanteResponseDTO from(Participante participante) {
        return new ParticipanteResponseDTO(
                participante.getId(),
                participante.getNome(),
                participante.getEmail(),
                participante.getDataCriacao()
        );
    }
}

package br.com.sge.sistemagestaoeventos.dto;

import br.com.sge.sistemagestaoeventos.enums.StatusInscricao;
import br.com.sge.sistemagestaoeventos.model.Inscricao;

import java.time.LocalDateTime;

public record InscricaoResponseDTO(
        String id,
        String eventoId,
        String participanteId,
        StatusInscricao status,
        LocalDateTime criadoEm
) {
    public static InscricaoResponseDTO from(Inscricao inscricao) {
        return new InscricaoResponseDTO(
                inscricao.getId(),
                inscricao.getEventoId(),
                inscricao.getParticipanteId(),
                inscricao.getStatus(),
                inscricao.getCriadoEm()
        );
    }
}
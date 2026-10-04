package br.com.sge.sistemagestaoeventos.dto;

import br.com.sge.sistemagestaoeventos.model.Participante;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ParticipanteResponseDTO(
        String id,
        String matricula,
        boolean matriculaAtiva,
        String nome,
        String email,
        LocalDate dataNascimento,
        LocalDateTime criadoEm
) {
    public static ParticipanteResponseDTO from(Participante participante) {
        return new ParticipanteResponseDTO(
                participante.getId(),
                participante.getMatricula(),
                participante.isMatriculaAtiva(),
                participante.getNome(),
                participante.getEmail(),
                participante.getDataNascimento(),
                participante.getCriadoEm()
        );
    }
}

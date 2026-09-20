package br.com.sge.sistemagestaoeventos.dto;

import br.com.sge.sistemagestaoeventos.model.Participante;

public record ParticipanteRequestDTO(
        String nome,
        String email
) {
    public Participante toParticipante() {
        return new Participante(nome, email);
    }
}

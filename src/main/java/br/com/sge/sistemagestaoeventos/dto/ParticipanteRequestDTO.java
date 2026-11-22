package br.com.sge.sistemagestaoeventos.dto;

import br.com.sge.sistemagestaoeventos.model.Participante;

import java.time.LocalDate;

public record ParticipanteRequestDTO(
        String nome,
        String email,
        String telefone,
        String matricula,
        LocalDate dataNascimento
) {
    public Participante toParticipante() {
        return new Participante(matricula, nome, email, telefone, dataNascimento);
    }
}

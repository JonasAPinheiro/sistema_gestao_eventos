package br.com.sge.sistemagestaoeventos.dto;

import br.com.sge.sistemagestaoeventos.model.Evento;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventoRequestDTO(
        String titulo,
        String descricao,
        LocalDate data,
        LocalTime horaInicio,
        LocalTime horaFim,
        String local,
        int capacidadeMaxima
) {
    public Evento toEvento() {
        return new Evento(titulo, descricao, data, horaInicio, horaFim, local, capacidadeMaxima);
    }
}
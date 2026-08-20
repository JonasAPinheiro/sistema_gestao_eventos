package br.com.sge.sistemagestaoeventos.dto;

import br.com.sge.sistemagestaoeventos.enums.StatusEvento;
import br.com.sge.sistemagestaoeventos.model.Evento;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record EventoResponseDTO(
        String id,
        String titulo,
        String descricao,
        LocalDate data,
        LocalTime horaInicio,
        LocalTime horaFim,
        String local,
        int capacidadeMaxima,
        StatusEvento status,
        LocalDateTime criadoEm
) {
    public static EventoResponseDTO from(Evento evento) {
        return new EventoResponseDTO(
                evento.getId(),
                evento.getTitulo(),
                evento.getDescricao(),
                evento.getData(),
                evento.getHoraInicio(),
                evento.getHoraFim(),
                evento.getLocal(),
                evento.getCapacidadeMaxima(),
                evento.getStatus(),
                evento.getCriadoEm()
        );
    }
}
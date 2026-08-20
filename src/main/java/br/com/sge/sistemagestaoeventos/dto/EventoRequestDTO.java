package br.com.sge.sistemagestaoeventos.dto;

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
) {}
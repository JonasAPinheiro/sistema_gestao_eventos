package br.com.sge.sistemagestaoeventos.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventoRequestDTO(
        String titulo,
        String descricao,
        LocalDate data,
        LocalTime horarioInicio,
        LocalTime horarioTermino,
        String local,
        int capacidadeMaxima
) {}
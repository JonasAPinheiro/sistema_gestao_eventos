package br.com.sge.sistemagestaoeventos.model;

import br.com.sge.sistemagestaoeventos.enums.TipoLote;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeEvento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record EventoDados(
        String titulo,
        String descricao,
        LocalDate data,
        LocalTime horaInicio,
        LocalTime horaFim,
        String local,
        int capacidadeMaxima,
        ModalidadeEvento modalidade,
        BigDecimal valorBase,
        TipoLote loteAtivo
) {}

package br.com.sge.sistemagestaoeventos.dto;

import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.EventoDados;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeEvento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;

public record EventoRequestDTO(
        String titulo,
        String descricao,
        LocalDate data,
        LocalTime horaInicio,
        LocalTime horaFim,
        String local,
        int capacidadeMaxima,
        TipoModalidade tipoModalidade,
        Integer idadeMinima,
        BigDecimal valorBase
) {

    public Evento toEvento(ModalidadeEvento modalidade) {
        return new Evento(
                new EventoDados(
                        titulo,
                        descricao,
                        data,
                        horaInicio,
                        horaFim,
                        local,
                        capacidadeMaxima,
                        modalidade,
                        valorBase
                )
        );
    }
}

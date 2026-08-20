package br.com.sge.sistemagestaoeventos.model;

import br.com.sge.sistemagestaoeventos.enums.StatusEvento;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
public class Evento {
    private final String id;
    @Setter
    private String titulo;
    @Setter
    private String descricao;
    @Setter
    private LocalDate data;
    @Setter
    private LocalTime horaInicio;
    @Setter
    private LocalTime horaFim;
    @Setter
    private String local;
    @Setter
    private int capacidadeMaxima;
    private StatusEvento status;
    private final LocalDateTime criadoEm;

    public Evento(String titulo, String descricao, LocalDate data, LocalTime horaInicio, LocalTime horaFim, String local, int capacidadeMaxima) {
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.descricao = descricao;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.local = local;
        this.capacidadeMaxima = capacidadeMaxima;
        this.status = StatusEvento.ATIVO;
        this.criadoEm = LocalDateTime.now();
    }

    public void cancelar() {
        this.status = StatusEvento.CANCELADO;
    }
}

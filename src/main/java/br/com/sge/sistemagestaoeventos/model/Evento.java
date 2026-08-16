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
    private LocalTime horarioInicio;
    @Setter
    private LocalTime horarioTermino;
    @Setter
    private String local;
    @Setter
    private int capacidadeMaxima;
    private StatusEvento status;
    private final LocalDateTime dataCriacao;

    public Evento(String titulo, String descricao, LocalDate data, LocalTime horarioInicio, LocalTime horarioTermino, String local, int capacidadeMaxima) {
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.descricao = descricao;
        this.data = data;
        this.horarioInicio = horarioInicio;
        this.horarioTermino = horarioTermino;
        this.local = local;
        this.capacidadeMaxima = capacidadeMaxima;
        this.status = StatusEvento.ATIVO;
        this.dataCriacao = LocalDateTime.now();
    }

    public void cancelar() {
        this.status = StatusEvento.CANCELADO;
    }
}

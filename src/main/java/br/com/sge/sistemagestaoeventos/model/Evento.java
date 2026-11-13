package br.com.sge.sistemagestaoeventos.model;

import br.com.sge.sistemagestaoeventos.enums.StatusEvento;
import br.com.sge.sistemagestaoeventos.enums.TipoLote;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeEvento;
import lombok.Getter;
import lombok.Setter;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.math.BigDecimal;
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
    private final ModalidadeEvento modalidade;
    @Setter
    private BigDecimal valorBase;
    @Setter
    private TipoLote loteAtivo;

    public Evento(EventoDados dados) {
        this.id = UUID.randomUUID().toString();
        this.titulo = dados.titulo();
        this.descricao = dados.descricao();
        this.data = dados.data();
        this.horaInicio = dados.horaInicio();
        this.horaFim = dados.horaFim();
        this.local = dados.local();
        this.capacidadeMaxima = dados.capacidadeMaxima();
        this.status = StatusEvento.ATIVO;
        this.criadoEm = LocalDateTime.now(Clock.systemDefaultZone());
        this.modalidade = dados.modalidade();
        this.valorBase = dados.valorBase() == null ? BigDecimal.ZERO : valorBase;
        this.loteAtivo = dados.loteAtivo() == null ? TipoLote.LOTE_2 : loteAtivo;
    }

    public void cancelar() {
        this.status = StatusEvento.CANCELADO;
    }
}

package br.com.sge.sistemagestaoeventos.model;

import br.com.sge.sistemagestaoeventos.enums.StatusInscricao;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Inscricao {
    private final String id;
    private final String eventoId;
    private final String participanteId;
    private final LocalDateTime criadoEm;
    private StatusInscricao status;

    public Inscricao(String eventoId, String participanteId) {
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.participanteId = participanteId;
        this.criadoEm = LocalDateTime.now();
        this.status = StatusInscricao.CONFIRMADA;
    }

    public void cancelar() {
        this.status = StatusInscricao.CANCELADA;
    }
}
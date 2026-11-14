package br.com.sge.sistemagestaoeventos.model;

import br.com.sge.sistemagestaoeventos.enums.StatusInscricao;
import br.com.sge.sistemagestaoeventos.exception.StatusInvalidoException;
import br.com.sge.sistemagestaoeventos.model.comprovante.Comprovante;
import lombok.Getter;
import lombok.Setter;

import java.time.Clock;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class Inscricao {
    private final String id;
    private final String eventoId;
    private final String participanteId;
    private String motivoCancelamento;
    private final LocalDateTime criadoEm;
    private StatusInscricao status;
    private final BigDecimal valorFinal;
    @Setter
    private Comprovante comprovante;


    public Inscricao(String eventoId, String participanteId, BigDecimal valorFinal) {
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.participanteId = participanteId;
        this.criadoEm = LocalDateTime.now(Clock.systemDefaultZone());
        this.status = StatusInscricao.PENDENTE;
        this.valorFinal = valorFinal;
    }

    public void validarPendente() {
        if (status != StatusInscricao.PENDENTE) {
            throw new StatusInvalidoException("Somente inscrições pendentes podem mudar de estado.");
        }
    }

    public void confirmar() {
        validarPendente();
        this.status = StatusInscricao.CONFIRMADA;
    }

    public void cancelar(String motivo) {
        validarPendente();
        this.status = StatusInscricao.CANCELADA;
        this.motivoCancelamento = motivo;
    }
}

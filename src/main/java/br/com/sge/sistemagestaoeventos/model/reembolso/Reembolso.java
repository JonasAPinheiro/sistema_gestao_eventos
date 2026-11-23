package br.com.sge.sistemagestaoeventos.model.reembolso;

import br.com.sge.sistemagestaoeventos.enums.DestinoReembolso;
import br.com.sge.sistemagestaoeventos.enums.MotivoReembolso;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

public record Reembolso(
        String id,
        String inscricaoId,
        String participanteId,
        BigDecimal valor,
        DestinoReembolso destino,
        MotivoReembolso motivo,
        LocalDateTime criadoEm
) {
    public static Reembolso criar(String inscricaoId, String participanteId, BigDecimal valor,
                                  DestinoReembolso destino, MotivoReembolso motivo) {
        return new Reembolso(UUID.randomUUID().toString(), inscricaoId, participanteId, valor,
                destino, motivo, LocalDateTime.now(Clock.systemDefaultZone()));
    }
}
package br.com.sge.sistemagestaoeventos.dto;

import br.com.sge.sistemagestaoeventos.enums.DestinoReembolso;
import br.com.sge.sistemagestaoeventos.enums.MotivoReembolso;
import br.com.sge.sistemagestaoeventos.model.reembolso.Reembolso;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReembolsoResponseDTO(
        String id,
        String inscricaoId,
        String participanteId,
        BigDecimal valor,
        DestinoReembolso destino,
        MotivoReembolso motivo,
        LocalDateTime criadoEm
) {
    public static ReembolsoResponseDTO from(Reembolso reembolso) {
        return new ReembolsoResponseDTO(reembolso.id(), reembolso.inscricaoId(), reembolso.participanteId(),
                reembolso.valor(), reembolso.destino(), reembolso.motivo(), reembolso.criadoEm());
    }
}
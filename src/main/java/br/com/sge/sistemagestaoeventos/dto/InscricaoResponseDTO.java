package br.com.sge.sistemagestaoeventos.dto;

import br.com.sge.sistemagestaoeventos.enums.FormaPagamento;
import br.com.sge.sistemagestaoeventos.enums.StatusInscricao;
import br.com.sge.sistemagestaoeventos.model.Inscricao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InscricaoResponseDTO(
        String id,
        String eventoId,
        String participanteId,
        StatusInscricao status,
        String motivoCancelamento,
        LocalDateTime criadoEm,
        BigDecimal valorFinal,
        ComprovanteResponseDTO comprovante,
        FormaPagamento formaPagamento,
        BigDecimal valorPagoCarteira,
        boolean reembolsada
) {

    public static InscricaoResponseDTO from(
            Inscricao inscricao
    ) {
        return new InscricaoResponseDTO(
                inscricao.getId(),
                inscricao.getEventoId(),
                inscricao.getParticipanteId(),
                inscricao.getStatus(),
                inscricao.getMotivoCancelamento(),
                inscricao.getCriadoEm(),
                inscricao.getValorFinal(),
                ComprovanteResponseDTO.from(inscricao.getComprovante()),
                inscricao.getFormaPagamento(),
                inscricao.getValorPagoCarteira(),
                inscricao.isReembolsada()
        );
    }
}
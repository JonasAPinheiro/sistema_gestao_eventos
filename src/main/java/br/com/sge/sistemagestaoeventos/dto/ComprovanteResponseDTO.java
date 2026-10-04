package br.com.sge.sistemagestaoeventos.dto;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.model.comprovante.Comprovante;

public record ComprovanteResponseDTO(
        String inscricaoId,
        TipoComprovante tipo,
        String conteudo,
        String payloadQrCode
) {
    public static ComprovanteResponseDTO from(Comprovante comprovante) {
        return new ComprovanteResponseDTO(
                comprovante.getInscricaoId(),
                comprovante.getTipo(),
                comprovante.getConteudo(),
                comprovante.obterPayloadQrCode().orElse(null)
        );
    }
}
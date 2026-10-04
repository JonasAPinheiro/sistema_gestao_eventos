package br.com.sge.sistemagestaoeventos.dto;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.model.comprovante.Comprovante;
import br.com.sge.sistemagestaoeventos.model.comprovante.ComprovanteDigital;
import br.com.sge.sistemagestaoeventos.model.comprovante.PayloadQrCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ComprovanteResponseDTOTest {

    @Test
    @DisplayName("Deve mapear comprovante simples sem payload de QR Code")
    void deveMapearComprovanteSimplesSemPayloadQrCode() {

        Comprovante comprovante = new Comprovante(
                "inscricao-1",
                TipoComprovante.SIMPLES,
                "COMPROVANTE DE INSCRIÇÃO"
        );

        ComprovanteResponseDTO dto =
                ComprovanteResponseDTO.from(comprovante);

        assertThat(dto.inscricaoId())
                .isEqualTo("inscricao-1");

        assertThat(dto.tipo())
                .isEqualTo(TipoComprovante.SIMPLES);

        assertThat(dto.conteudo())
                .isEqualTo("COMPROVANTE DE INSCRIÇÃO");

        assertThat(dto.payloadQrCode())
                .isNull();
    }

    @Test
    @DisplayName("Deve mapear comprovante digital completo com payload de QR Code")
    void deveMapearComprovanteDigitalComPayloadQrCode() {

        PayloadQrCode payload = new PayloadQrCode(
                "hash-123",
                "inscricao-1",
                "evento-1",
                "participante-1"
        );

        ComprovanteDigital comprovante = new ComprovanteDigital(
                "inscricao-1",
                "COMPROVANTE DIGITAL DE INSCRIÇÃO",
                payload
        );

        ComprovanteResponseDTO dto =
                ComprovanteResponseDTO.from(comprovante);

        assertThat(dto.inscricaoId())
                .isEqualTo("inscricao-1");

        assertThat(dto.tipo())
                .isEqualTo(TipoComprovante.DIGITAL_COMPLETO);

        assertThat(dto.conteudo())
                .isEqualTo("COMPROVANTE DIGITAL DE INSCRIÇÃO");

        assertThat(dto.payloadQrCode())
                .isEqualTo(payload.toString())
                .contains("SGE-QR")
                .contains("hash=hash-123");
    }
}

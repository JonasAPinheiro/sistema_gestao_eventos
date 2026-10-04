package br.com.sge.sistemagestaoeventos.model.comprovante;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ComprovanteDigitalTest {

    @Test
    @DisplayName("Deve retornar o payload QR Code do comprovante digital")
    void deveRetornarPayloadQrCode() {

        PayloadQrCode payload = new PayloadQrCode(
                "hash-123",
                "inscricao-1",
                "evento-1",
                "participante-1"
        );

        ComprovanteDigital comprovante = new ComprovanteDigital(
                "inscricao-1",
                "conteudo do comprovante",
                payload
        );

        Optional<String> resultado =
                comprovante.obterPayloadQrCode();

        assertThat(resultado)
                .isPresent()
                .contains(payload.toString());
    }

    @Test
    @DisplayName("Deve possuir tipo DIGITAL_COMPLETO")
    void devePossuirTipoDigitalCompleto() {

        PayloadQrCode payload = new PayloadQrCode(
                "hash-123",
                "inscricao-1",
                "evento-1",
                "participante-1"
        );

        ComprovanteDigital comprovante = new ComprovanteDigital(
                "inscricao-1",
                "conteudo",
                payload
        );

        assertThat(comprovante.getTipo())
                .isEqualTo(
                        br.com.sge.sistemagestaoeventos.enums.TipoComprovante.DIGITAL_COMPLETO
                );
    }
}
package br.com.sge.sistemagestaoeventos.model.comprovante;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PayloadQrCodeTest {

    @Test
    @DisplayName("Deve montar o payload do QR Code no formato esperado")
    void deveMontarPayloadQrCode() {

        PayloadQrCode payload = new PayloadQrCode(
                "hash-123",
                "inscricao-1",
                "evento-1",
                "participante-1"
        );

        assertThat(payload.toString())
                .hasToString(
                        "SGE-QR|inscricao=inscricao-1|evento=evento-1|participante=participante-1|hash=hash-123"
                );
    }
}
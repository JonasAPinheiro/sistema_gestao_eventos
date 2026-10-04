package br.com.sge.sistemagestaoeventos.model.comprovante;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ComprovanteTest {

    @Test
    @DisplayName("Comprovante simples não deve possuir payload de QR Code")
    void comprovanteSimplesNaoDevePossuirPayloadQrCode() {

        Comprovante comprovante = new Comprovante(
                "inscricao-1",
                TipoComprovante.SIMPLES,
                "COMPROVANTE DE INSCRIÇÃO"
        );

        assertThat(comprovante.getTipo())
                .isEqualTo(TipoComprovante.SIMPLES);

        assertThat(comprovante.obterPayloadQrCode())
                .isEmpty();
    }
}

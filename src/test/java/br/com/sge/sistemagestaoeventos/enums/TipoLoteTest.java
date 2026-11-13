package br.com.sge.sistemagestaoeventos.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class TipoLoteTest {

    @ParameterizedTest
    @CsvSource({
            "0, 100, PROMOCIONAL",
            "9, 100, PROMOCIONAL",
            "10, 100, LOTE_1",
            "59, 100, LOTE_1",
            "60, 100, LOTE_2",
            "99, 100, LOTE_2",
            "100, 100, LOTE_2",
            "0, 3, PROMOCIONAL",
            "1, 3, LOTE_1",
            "2, 3, LOTE_2"
    })
    @DisplayName("Deve resolver o lote conforme o percentual de ingressos vendidos")
    void deveResolverLoteConformeIngressosVendidos(long vendidos, int capacidade, TipoLote esperado) {
        assertThat(TipoLote.paraIngressosVendidos(vendidos, capacidade)).isEqualTo(esperado);
    }
}

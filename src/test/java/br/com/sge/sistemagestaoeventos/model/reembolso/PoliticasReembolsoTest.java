package br.com.sge.sistemagestaoeventos.model.reembolso;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PoliticasReembolsoTest {
    private final LocalDateTime inicio = LocalDateTime.of(2026, 12, 20, 18, 0);
    private final ReembolsoIntegral integral = new ReembolsoIntegral();
    private final ReembolsoParcial parcial = new ReembolsoParcial();

    @Test
    @DisplayName("Integral: aplicável com exatamente 72h de antecedência, não com menos")
    void integralNoLimite() {
        assertTrue(integral.aplicavel(inicio, inicio.minusHours(72)));
        assertTrue(integral.aplicavel(inicio, inicio.minusDays(10)));
        assertFalse(integral.aplicavel(inicio, inicio.minusHours(72).plusMinutes(1)));
    }

    @Test
    @DisplayName("Parcial: aplicável entre 72h (exclusive) e 24h (inclusive)")
    void parcialNaFaixa() {
        assertTrue(parcial.aplicavel(inicio, inicio.minusHours(72).plusMinutes(1)));
        assertTrue(parcial.aplicavel(inicio, inicio.minusHours(24)));
        assertFalse(parcial.aplicavel(inicio, inicio.minusHours(72)));
        assertFalse(parcial.aplicavel(inicio, inicio.minusHours(24).plusMinutes(1)));
    }

    @Test
    @DisplayName("Integral devolve 100% e parcial devolve 50%")
    void valoresCalculados() {
        assertEquals(new BigDecimal("100.00"), integral.calcular(new BigDecimal("100.00")));
        assertEquals(new BigDecimal("50.00"), parcial.calcular(new BigDecimal("100.00")));
    }
}
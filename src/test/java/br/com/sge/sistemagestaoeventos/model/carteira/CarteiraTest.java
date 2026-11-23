
package br.com.sge.sistemagestaoeventos.model.carteira;

import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CarteiraTest {

    @Test
    void creditaEDebitaSaldo() {
        Carteira carteira = new Carteira("p1");
        carteira.creditar(new BigDecimal("50.00"));
        carteira.debitar(new BigDecimal("20.00"));

        assertEquals(new BigDecimal("30.00"), carteira.getSaldo());
    }

    @Test
    void saldoInsuficienteLancaRegraNegocioException() {
        Carteira carteira = new Carteira("p1");
        carteira.creditar(new BigDecimal("10.00"));

        BigDecimal valorDebito = new BigDecimal("10.01");

        assertThrows(
                RegraNegocioException.class,
                () -> carteira.debitar(valorDebito)
        );
    }

    @Test
    void creditoComValorZeroLancaRegraNegocioException() {
        Carteira carteira = new Carteira("p1");
        BigDecimal valorCredito = BigDecimal.ZERO;

        assertThrows(
                RegraNegocioException.class,
                () -> carteira.creditar(valorCredito)
        );
    }

    @Test
    void debitoComValorNuloLancaRegraNegocioException() {
        Carteira carteira = new Carteira("p1");
        BigDecimal valorDebito = null;

        assertThrows(
                RegraNegocioException.class,
                () -> carteira.debitar(valorDebito)
        );
    }
}

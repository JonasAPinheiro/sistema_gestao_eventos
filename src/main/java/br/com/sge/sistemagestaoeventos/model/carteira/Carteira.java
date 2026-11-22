package br.com.sge.sistemagestaoeventos.model.carteira;

import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class Carteira {
    private final String participanteId;
    private BigDecimal saldo;

    public Carteira(String participanteId) {
        this.participanteId = participanteId;
        this.saldo = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public void creditar(BigDecimal valor) {
        validarValor(valor, "creditar");
        this.saldo = saldo.add(valor).setScale(2, RoundingMode.HALF_UP);
    }

    public void debitar(BigDecimal valor) {
        validarValor(valor, "debitar");
        if (saldo.compareTo(valor) < 0) {
            throw new RegraNegocioException("Saldo insuficiente na carteira.");
        }
        this.saldo = saldo.subtract(valor).setScale(2, RoundingMode.HALF_UP);
    }

    private void validarValor(BigDecimal valor, String operacao) {
        if (valor == null || valor.signum() <= 0) {
            throw new RegraNegocioException("O valor a " + operacao + " deve ser maior que zero.");
        }
    }
}
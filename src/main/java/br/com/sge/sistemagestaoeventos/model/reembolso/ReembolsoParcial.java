package br.com.sge.sistemagestaoeventos.model.reembolso;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Component
public class ReembolsoParcial implements PoliticaReembolso {
    private static final long HORAS_LIMITE_INTEGRAL = 72;
    private static final long HORAS_LIMITE_MINIMO = 24;

    @Override
    public boolean aplicavel(LocalDateTime inicioEvento, LocalDateTime momentoSolicitacao) {
        boolean dentroDe72h = momentoSolicitacao.isAfter(inicioEvento.minusHours(HORAS_LIMITE_INTEGRAL));
        boolean ate24hAntes = !momentoSolicitacao.isAfter(inicioEvento.minusHours(HORAS_LIMITE_MINIMO));
        return dentroDe72h && ate24hAntes;
    }

    @Override
    public BigDecimal calcular(BigDecimal valorFinal) {
        return valorFinal.multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_UP);
    }
}
package br.com.sge.sistemagestaoeventos.model.reembolso;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;


@Component
public class ReembolsoIntegral implements PoliticaReembolso {
    private static final long HORAS_MINIMAS = 72;

    @Override
    public boolean aplicavel(LocalDateTime inicioEvento, LocalDateTime momentoSolicitacao) {
        return !momentoSolicitacao.isAfter(inicioEvento.minusHours(HORAS_MINIMAS));
    }

    @Override
    public BigDecimal calcular(BigDecimal valorFinal) {
        return valorFinal.setScale(2, RoundingMode.HALF_UP);
    }
}
package br.com.sge.sistemagestaoeventos.model.reembolso;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PoliticaReembolso {
    boolean aplicavel(LocalDateTime inicioEvento, LocalDateTime momentoSolicitacao);

    BigDecimal calcular(BigDecimal valorFinal);
}
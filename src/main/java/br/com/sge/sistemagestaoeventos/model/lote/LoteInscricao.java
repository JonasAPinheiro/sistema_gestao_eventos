package br.com.sge.sistemagestaoeventos.model.lote;

import br.com.sge.sistemagestaoeventos.enums.TipoLote;

import java.math.BigDecimal;

public interface LoteInscricao {
    TipoLote getTipo();

    BigDecimal calcularValorFinal(BigDecimal valorBase);
}

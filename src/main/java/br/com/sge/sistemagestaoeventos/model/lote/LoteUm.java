package br.com.sge.sistemagestaoeventos.model.lote;

import br.com.sge.sistemagestaoeventos.enums.TipoLote;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class LoteUm implements LoteInscricao {
    @Override
    public TipoLote getTipo() {
        return TipoLote.LOTE_1;
    }

    @Override
    public BigDecimal calcularValorFinal(BigDecimal valorBase) {
        return valorBase.multiply(new BigDecimal("0.90")).setScale(2, RoundingMode.HALF_UP);
    }
}

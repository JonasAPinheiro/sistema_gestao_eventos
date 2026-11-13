package br.com.sge.sistemagestaoeventos.model.lote;

import br.com.sge.sistemagestaoeventos.enums.TipoLote;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class LotePromocional implements LoteInscricao {
    @Override
    public TipoLote getTipo() {
        return TipoLote.PROMOCIONAL;
    }

    @Override
    public BigDecimal calcularValorFinal(BigDecimal valorBase) {
        return valorBase.multiply(new BigDecimal("0.80")).setScale(2, RoundingMode.HALF_UP);
    }
}

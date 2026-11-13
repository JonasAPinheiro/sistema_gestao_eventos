package br.com.sge.sistemagestaoeventos.model.lote;

import br.com.sge.sistemagestaoeventos.enums.TipoLote;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class LoteDois implements LoteInscricao {
    @Override
    public TipoLote getTipo() {
        return TipoLote.LOTE_2;
    }

    @Override
    public BigDecimal calcularValorFinal(BigDecimal valorBase) {
        return valorBase.setScale(2, RoundingMode.HALF_UP);
    }
}

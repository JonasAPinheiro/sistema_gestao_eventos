package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.enums.TipoLote;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.lote.LoteInscricao;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CalculadoraValorInscricao {
    private final Map<TipoLote, LoteInscricao> lotesPorTipo;

    public CalculadoraValorInscricao(List<LoteInscricao> lotes) {
        this.lotesPorTipo = lotes.stream().collect(Collectors.toMap(LoteInscricao::getTipo, Function.identity()));
    }

    public BigDecimal calcular(Evento evento) {
        if (evento.getModalidade().isGratuita()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        LoteInscricao lote = lotesPorTipo.get(evento.getLoteAtivo());
        if (lote == null) {
            throw new IllegalStateException("Não há estratégia cadastrada para o lote ativo.");
        }
        return lote.calcularValorFinal(evento.getValorBase());
    }
}

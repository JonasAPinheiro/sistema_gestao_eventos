package br.com.sge.sistemagestaoeventos.model.modalidade.factory;

import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeEvento;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ModalidadeEventoFactory {

    private final Map<TipoModalidade, CriadorModalidade> criadoresPorTipo;

    public ModalidadeEventoFactory(List<CriadorModalidade> criadores) {
        this.criadoresPorTipo = criadores.stream()
                .collect(Collectors.toMap(CriadorModalidade::getTipo, Function.identity()));
    }

    public ModalidadeEvento criar(TipoModalidade tipo, Integer idadeMinima) {
        TipoModalidade tipoEfetivo = tipo != null ? tipo : TipoModalidade.ABERTO;
        CriadorModalidade criador = criadoresPorTipo.get(tipoEfetivo);

        if (criador == null) {
            throw new IllegalStateException("Nenhum criador configurado para a modalidade " + tipoEfetivo + ".");
        }
        return criador.criar(idadeMinima);
    }
}

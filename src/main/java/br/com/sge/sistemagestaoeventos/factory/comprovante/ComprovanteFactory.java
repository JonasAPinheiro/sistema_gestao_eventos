package br.com.sge.sistemagestaoeventos.factory.comprovante;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.model.comprovante.EmissorComprovante;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ComprovanteFactory {

    private final Map<TipoComprovante, EmissorComprovante> emissoresPorTipo;

    public ComprovanteFactory(List<EmissorComprovante> emissores) {
        this.emissoresPorTipo = emissores.stream()
                .collect(Collectors.toMap(
                        EmissorComprovante::getTipo,
                        Function.identity()
                ));
    }

    public EmissorComprovante obterEmissor(TipoComprovante tipo) {
        EmissorComprovante emissor = emissoresPorTipo.get(tipo);

        if (emissor == null) {
            throw new IllegalStateException("Nenhum emissor de comprovante configurado para o tipo " + tipo + ".");
        }

        return emissor;
    }
}

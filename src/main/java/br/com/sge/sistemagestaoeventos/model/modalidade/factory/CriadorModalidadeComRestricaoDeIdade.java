package br.com.sge.sistemagestaoeventos.model.modalidade.factory;

import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeComRestricaoDeIdade;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeEvento;
import org.springframework.stereotype.Component;

@Component
public class CriadorModalidadeComRestricaoDeIdade implements CriadorModalidade {
    @Override
    public TipoModalidade getTipo() { return TipoModalidade.RESTRICAO_IDADE; }

    @Override
    public ModalidadeEvento criar(Integer idadeMinima) {
        return new ModalidadeComRestricaoDeIdade(idadeMinima);
    }
}

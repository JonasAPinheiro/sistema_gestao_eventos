package br.com.sge.sistemagestaoeventos.model.modalidade.factory;

import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeAberta;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeEvento;
import org.springframework.stereotype.Component;

@Component
public class CriadorModalidadeAberta implements CriadorModalidade {
    @Override
    public TipoModalidade getTipo() { return TipoModalidade.ABERTO; }

    @Override
    public ModalidadeEvento criar(Integer idadeMinima) { return new ModalidadeAberta(); }
}

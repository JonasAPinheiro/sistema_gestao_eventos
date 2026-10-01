package br.com.sge.sistemagestaoeventos.model.modalidade;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;

public class ModalidadeAberta implements ModalidadeEvento {
    @Override
    public TipoModalidade getTipo() {
        return TipoModalidade.ABERTO;
    }

    @Override
    public TipoComprovante getTipoComprovante() {
        return TipoComprovante.SIMPLES;
    }
}

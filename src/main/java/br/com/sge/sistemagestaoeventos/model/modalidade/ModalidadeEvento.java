package br.com.sge.sistemagestaoeventos.model.modalidade;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;

public interface ModalidadeEvento {
    TipoModalidade getTipo();
    TipoComprovante getTipoComprovante();
}

package br.com.sge.sistemagestaoeventos.factory.modalidade;

import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeEvento;

public interface CriadorModalidade {
    TipoModalidade getTipo();
    ModalidadeEvento criar(Integer idadeMinima);
}

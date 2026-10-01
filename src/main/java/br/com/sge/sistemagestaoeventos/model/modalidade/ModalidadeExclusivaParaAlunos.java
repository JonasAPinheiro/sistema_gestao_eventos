package br.com.sge.sistemagestaoeventos.model.modalidade;

import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;

public class ModalidadeExclusivaParaAlunos implements ModalidadeEvento {
    @Override
    public TipoModalidade getTipo() {
        return TipoModalidade.EXCLUSIVO_ALUNOS;
    }
}

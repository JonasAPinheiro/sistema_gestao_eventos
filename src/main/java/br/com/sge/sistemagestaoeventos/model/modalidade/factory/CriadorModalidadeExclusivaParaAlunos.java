package br.com.sge.sistemagestaoeventos.model.modalidade.factory;

import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeEvento;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeExclusivaParaAlunos;

public class CriadorModalidadeExclusivaParaAlunos implements CriadorModalidade{
    @Override
    public TipoModalidade getTipo() { return TipoModalidade.EXCLUSIVO_ALUNOS; }

    @Override
    public ModalidadeEvento criar(Integer idadeMinima) { return new ModalidadeExclusivaParaAlunos(); }
}

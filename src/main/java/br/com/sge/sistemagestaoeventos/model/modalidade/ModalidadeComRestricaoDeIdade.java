package br.com.sge.sistemagestaoeventos.model.modalidade;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import lombok.Getter;

@Getter
public class ModalidadeComRestricaoDeIdade implements ModalidadeEvento {
    private final int idadeMinima;

    public ModalidadeComRestricaoDeIdade(Integer idadeMinima) {
        if (idadeMinima == null || idadeMinima <= 0) {
            throw new RegraNegocioException("A idade mínima do evento deve ser um número inteiro maior que zero.");
        }
        this.idadeMinima = idadeMinima;
    }

    @Override
    public TipoModalidade getTipo() {
        return TipoModalidade.RESTRICAO_IDADE;
    }

    @Override
    public TipoComprovante getTipoComprovante() {
        return TipoComprovante.DIGITAL_COMPLETO;
    }
}

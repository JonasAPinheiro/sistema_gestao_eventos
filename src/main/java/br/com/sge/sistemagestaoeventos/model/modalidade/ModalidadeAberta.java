package br.com.sge.sistemagestaoeventos.model.modalidade;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;

import java.time.LocalDateTime;

public class ModalidadeAberta implements ModalidadeEvento {
    @Override
    public TipoModalidade getTipo() {
        return TipoModalidade.ABERTO;
    }

    @Override
    public TipoComprovante getTipoComprovante() {
        return TipoComprovante.SIMPLES;
    }

    @Override
    public void validarCancelamento(LocalDateTime inicioEvento, LocalDateTime momentoCancelamento, String motivo) {
        if (!momentoCancelamento.isBefore(inicioEvento)) {
            throw new RegraNegocioException("Não é possível cancelar a inscrição após o início do evento.");
        }
    }
}

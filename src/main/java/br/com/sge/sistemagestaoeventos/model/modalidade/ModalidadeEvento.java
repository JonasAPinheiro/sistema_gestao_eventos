package br.com.sge.sistemagestaoeventos.model.modalidade;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;

import java.time.LocalDateTime;

public interface ModalidadeEvento {
    TipoModalidade getTipo();
    TipoComprovante getTipoComprovante();

    void validarCancelamento(LocalDateTime inicioEvento, LocalDateTime momentoCancelamento, String motivo);
}

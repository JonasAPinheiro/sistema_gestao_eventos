package br.com.sge.sistemagestaoeventos.model.modalidade;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Participante;

import java.time.LocalDateTime;

public class ModalidadeExclusivaParaAlunos implements ModalidadeEvento, ValidadorElegibilidade {
    @Override
    public TipoModalidade getTipo() {
        return TipoModalidade.EXCLUSIVO_ALUNOS;
    }

    @Override
    public TipoComprovante getTipoComprovante() {
        return TipoComprovante.DIGITAL_COMPLETO;
    }

    @Override
    public void validarElegibilidade(Participante participante) {
        if (participante.getMatricula() == null || participante.getMatricula().isBlank() || !participante.isMatriculaAtiva()) {
            throw new RegraNegocioException("O participante deve possuir uma matrícula ativa para se inscrever.");
        }
    }

    @Override
    public void validarCancelamento(LocalDateTime inicioEvento, LocalDateTime momentoCancelamento, String motivo) {
        LocalDateTime limiteCancelamento = inicioEvento.minusHours(24);

        if (momentoCancelamento.isAfter(limiteCancelamento)) {
            throw new RegraNegocioException("O cancelamento deve ser solicitado com antecedência mínima de 24 horas.");
        }
    }
}

package br.com.sge.sistemagestaoeventos.model.modalidade;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Participante;
import lombok.Getter;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;

@Getter
public class ModalidadeComRestricaoDeIdade implements ModalidadeEvento, ValidadorElegibilidade {
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

    @Override
    public void validarElegibilidade(Participante participante) {
        if (participante.getDataNascimento() == null) {
            throw new RegraNegocioException("O participante deve possuir data de nascimento informada.");
        }

        int idade = Period.between(
                participante.getDataNascimento(),
                LocalDate.now(Clock.systemDefaultZone())
        ).getYears();

        if (idade < idadeMinima) {
            throw new RegraNegocioException(
                    "O participante não possui a idade mínima exigida para este evento."
            );
        }
    }
}

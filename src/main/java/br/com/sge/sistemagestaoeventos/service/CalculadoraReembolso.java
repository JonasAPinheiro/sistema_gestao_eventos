package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.reembolso.PoliticaReembolso;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CalculadoraReembolso {
    private final List<PoliticaReembolso> politicas;

    public CalculadoraReembolso(List<PoliticaReembolso> politicas) {
        this.politicas = politicas;
    }

    public BigDecimal calcular(Evento evento, Inscricao inscricao, LocalDateTime momentoSolicitacao) {
        if (evento.getModalidade().isGratuita() || inscricao.getValorFinal().signum() <= 0) {
            throw new RegraNegocioException("Eventos gratuitos não possuem reembolso.");
        }

        LocalDateTime inicioEvento = LocalDateTime.of(evento.getData(), evento.getHoraInicio());

        return politicas.stream()
                .filter(politica -> politica.aplicavel(inicioEvento, momentoSolicitacao))
                .findFirst()
                .map(politica -> politica.calcular(inscricao.getValorFinal()))
                .orElseThrow(() -> new RegraNegocioException(
                        "O reembolso deve ser solicitado com antecedência mínima de 24 horas."));
    }
}
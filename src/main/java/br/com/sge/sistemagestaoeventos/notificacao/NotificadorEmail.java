package br.com.sge.sistemagestaoeventos.notificacao;

import br.com.sge.sistemagestaoeventos.enums.CanalNotificacao;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.model.reembolso.Reembolso;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificadorEmail implements Notificador {
    private static final Logger log = LoggerFactory.getLogger(NotificadorEmail.class);

    @Override
    public CanalNotificacao canal() {
        return CanalNotificacao.EMAIL;
    }

    @Override
    public void notificar(Inscricao inscricao, Participante participante) {
        log.info("E-mail simulado enviado para {} sobre a inscrição {}", participante.getEmail(), inscricao.getId());
    }

    @Override
    public void notificarReembolso(Inscricao inscricao, Participante participante, Reembolso reembolso) {
        log.info("E-mail simulado enviado para {} sobre o reembolso de R$ {} da inscrição {}",
                participante.getEmail(), reembolso.valor(), inscricao.getId());
    }
}
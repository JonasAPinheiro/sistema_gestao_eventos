package br.com.sge.sistemagestaoeventos.notificacao;

import br.com.sge.sistemagestaoeventos.enums.CanalNotificacao;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificadorSms implements Notificador {
    private static final Logger log = LoggerFactory.getLogger(NotificadorSms.class);

    @Override
    public CanalNotificacao canal() {
        return CanalNotificacao.SMS;
    }

    @Override
    public void notificar(Inscricao inscricao, Participante participante) {
        log.info("SMS simulado enviado para {} sobre a inscrição {}", participante.getTelefone(), inscricao.getId());
    }
}

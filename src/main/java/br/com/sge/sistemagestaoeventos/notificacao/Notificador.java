package br.com.sge.sistemagestaoeventos.notificacao;

import br.com.sge.sistemagestaoeventos.enums.CanalNotificacao;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;

public interface Notificador {
    CanalNotificacao canal();

    void notificar(Inscricao inscricao, Participante participante);
}

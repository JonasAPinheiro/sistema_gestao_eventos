package br.com.sge.sistemagestaoeventos.dto;

import br.com.sge.sistemagestaoeventos.enums.CanalNotificacao;
import br.com.sge.sistemagestaoeventos.enums.StatusInscricao;
import br.com.sge.sistemagestaoeventos.enums.StatusNotificacao;
import br.com.sge.sistemagestaoeventos.model.notificacao.Notificacao;

import java.time.LocalDateTime;

public record NotificacaoResponseDTO(String id, String inscricaoId, String participanteId, String destinatario,
                                     CanalNotificacao canal, StatusInscricao statusInscricao, String mensagem,
                                     StatusNotificacao status, String erro, LocalDateTime criadaEm) {
    public static NotificacaoResponseDTO from(Notificacao notificacao) {
        return new NotificacaoResponseDTO(notificacao.id(), notificacao.inscricaoId(), notificacao.participanteId(),
                notificacao.destinatario(), notificacao.canal(), notificacao.statusInscricao(), notificacao.mensagem(),
                notificacao.status(), notificacao.erro(), notificacao.criadaEm());
    }
}

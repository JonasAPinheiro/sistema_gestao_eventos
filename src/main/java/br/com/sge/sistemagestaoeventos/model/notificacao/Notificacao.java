package br.com.sge.sistemagestaoeventos.model.notificacao;

import br.com.sge.sistemagestaoeventos.enums.CanalNotificacao;
import br.com.sge.sistemagestaoeventos.enums.StatusInscricao;
import br.com.sge.sistemagestaoeventos.enums.StatusNotificacao;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

public record Notificacao(
        String id,
        String inscricaoId,
        String participanteId,
        String destinatario,
        CanalNotificacao canal,
        StatusInscricao statusInscricao,
        String mensagem,
        StatusNotificacao status,
        String erro,
        LocalDateTime criadaEm
) {
    public static Notificacao criar(String inscricaoId, String participanteId, String destinatario,
                                    CanalNotificacao canal, StatusInscricao statusInscricao,
                                    String mensagem, StatusNotificacao status, String erro) {
        return new Notificacao(UUID.randomUUID().toString(), inscricaoId, participanteId, destinatario,
                canal, statusInscricao, mensagem, status, erro, LocalDateTime.now(Clock.systemDefaultZone()));
    }
}

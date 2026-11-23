package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.enums.CanalNotificacao;
import br.com.sge.sistemagestaoeventos.enums.StatusNotificacao;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.model.notificacao.Notificacao;
import br.com.sge.sistemagestaoeventos.model.reembolso.Reembolso;
import br.com.sge.sistemagestaoeventos.notificacao.Notificador;
import br.com.sge.sistemagestaoeventos.repository.NotificacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
public class NotificacaoService {
    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);

    private final List<Notificador> notificadores;
    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoService(List<Notificador> notificadores, NotificacaoRepository notificacaoRepository) {
        this.notificadores = notificadores;
        this.notificacaoRepository = notificacaoRepository;
    }

    public void notificarAlteracaoStatus(Inscricao inscricao, Participante participante) {
        String mensagem = "Sua inscrição foi " + inscricao.getStatus() + ".";
        disparar(inscricao, participante, mensagem, n -> n.notificar(inscricao, participante));
    }

    public void notificarReembolso(Inscricao inscricao, Participante participante, Reembolso reembolso) {
        String mensagem = "Reembolso de R$ " + reembolso.valor() + " processado (" + reembolso.destino() + ").";
        disparar(inscricao, participante, mensagem, n -> n.notificarReembolso(inscricao, participante, reembolso));
    }

    public List<Notificacao> listarTodas() {
        return notificacaoRepository.listarTodas();
    }

    // RN11: falha de um notificador nunca interrompe o fluxo de domínio.
    private void disparar(Inscricao inscricao, Participante participante, String mensagem,
                          Consumer<Notificador> envio) {
        for (Notificador notificador : notificadores) {
            try {
                envio.accept(notificador);
                salvarHistorico(inscricao, participante, notificador.canal(), mensagem, StatusNotificacao.ENVIADA, null);
            } catch (RuntimeException ex) {
                log.warn("Falha ao enviar notificação {} para a inscrição {}", notificador.canal(), inscricao.getId(), ex);
                salvarHistorico(inscricao, participante, notificador.canal(), mensagem, StatusNotificacao.FALHA, ex.getMessage());
            }
        }
    }

    private void salvarHistorico(Inscricao inscricao, Participante participante, CanalNotificacao canal,
                                 String mensagem, StatusNotificacao status, String erro) {
        String destinatario = canal == CanalNotificacao.EMAIL ? participante.getEmail() : participante.getTelefone();
        notificacaoRepository.salvar(Notificacao.criar(inscricao.getId(), participante.getId(), destinatario,
                canal, inscricao.getStatus(), mensagem, status, erro));
    }
}
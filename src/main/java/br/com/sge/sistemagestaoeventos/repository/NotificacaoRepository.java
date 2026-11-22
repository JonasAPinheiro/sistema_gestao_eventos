package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.model.notificacao.Notificacao;

import java.util.List;

public interface NotificacaoRepository {
    Notificacao salvar(Notificacao notificacao);

    List<Notificacao> listarTodas();
}

package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.enums.CanalNotificacao;
import br.com.sge.sistemagestaoeventos.enums.StatusInscricao;
import br.com.sge.sistemagestaoeventos.enums.StatusNotificacao;
import br.com.sge.sistemagestaoeventos.model.notificacao.Notificacao;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NotificacaoRepositoryMemoriaTest {

    @Test
    void deveSalvarEListarNotificacoesDaMaisRecenteParaAMaisAntiga() {
        NotificacaoRepositoryMemoria repository = new NotificacaoRepositoryMemoria();
        Notificacao antiga = notificacao("notificacao-1", LocalDateTime.of(2026, 11, 20, 10, 0));
        Notificacao recente = notificacao("notificacao-2", LocalDateTime.of(2026, 11, 20, 11, 0));

        assertThat(repository.salvar(antiga)).isSameAs(antiga);
        assertThat(repository.salvar(recente)).isSameAs(recente);

        List<Notificacao> resultado = repository.listarTodas();

        assertThat(resultado).containsExactly(recente, antiga);
    }

    private static Notificacao notificacao(String id, LocalDateTime criadaEm) {
        return new Notificacao(id, "inscricao-1", "participante-1", "maria@email.com", CanalNotificacao.EMAIL,
                StatusInscricao.CONFIRMADA, "Sua inscrição foi CONFIRMADA.", StatusNotificacao.ENVIADA,
                null, criadaEm);
    }
}

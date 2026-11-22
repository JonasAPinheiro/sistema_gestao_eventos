package br.com.sge.sistemagestaoeventos.notificacao;

import br.com.sge.sistemagestaoeventos.enums.CanalNotificacao;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class NotificadoresTest {

    @Test
    void deveEnviarEmailSimulado() {
        NotificadorEmail notificador = new NotificadorEmail();

        assertThat(notificador.canal()).isEqualTo(CanalNotificacao.EMAIL);
        assertThatCode(() -> notificador.notificar(inscricao(), participante())).doesNotThrowAnyException();
    }

    @Test
    void deveEnviarSmsSimulado() {
        NotificadorSms notificador = new NotificadorSms();

        assertThat(notificador.canal()).isEqualTo(CanalNotificacao.SMS);
        assertThatCode(() -> notificador.notificar(inscricao(), participante())).doesNotThrowAnyException();
    }

    private static Inscricao inscricao() {
        return new Inscricao("evento-1", "participante-1", BigDecimal.ZERO);
    }

    private static Participante participante() {
        return new Participante("12345", "Maria", "maria@email.com", "11999999999", LocalDate.of(1995, 1, 1));
    }
}

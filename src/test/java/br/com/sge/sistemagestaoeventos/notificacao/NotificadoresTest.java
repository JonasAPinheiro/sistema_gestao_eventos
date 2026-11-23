
package br.com.sge.sistemagestaoeventos.notificacao;

import br.com.sge.sistemagestaoeventos.enums.CanalNotificacao;
import br.com.sge.sistemagestaoeventos.enums.DestinoReembolso;
import br.com.sge.sistemagestaoeventos.enums.MotivoReembolso;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.model.reembolso.Reembolso;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class NotificadoresTest {

    @Test
    void deveEnviarEmailSimulado() {
        NotificadorEmail notificador = new NotificadorEmail();
        Inscricao inscricao = inscricao();
        Participante participante = participante();
        Reembolso reembolso = reembolso();

        assertThat(notificador.canal()).isEqualTo(CanalNotificacao.EMAIL);
        assertThatCode(() -> notificador.notificar(inscricao, participante))
                .doesNotThrowAnyException();
        assertThatCode(() -> notificador.notificarReembolso(inscricao, participante, reembolso))
                .doesNotThrowAnyException();
    }

    @Test
    void deveEnviarSmsSimulado() {
        NotificadorSms notificador = new NotificadorSms();
        Inscricao inscricao = inscricao();
        Participante participante = participante();
        Reembolso reembolso = reembolso();

        assertThat(notificador.canal()).isEqualTo(CanalNotificacao.SMS);
        assertThatCode(() -> notificador.notificar(inscricao, participante))
                .doesNotThrowAnyException();
        assertThatCode(() -> notificador.notificarReembolso(inscricao, participante, reembolso))
                .doesNotThrowAnyException();
    }

    @Test
    void padraoDeNotificarReembolsoNaoFazNada() {
        Notificador notificador = new Notificador() {
            @Override
            public CanalNotificacao canal() {
                return CanalNotificacao.EMAIL;
            }

            @Override
            public void notificar(Inscricao inscricao, Participante participante) {
                // Implementação vazia intencional para testar o método padrão da interface.
            }
        };

        Inscricao inscricao = inscricao();
        Participante participante = participante();
        Reembolso reembolso = reembolso();

        assertThatCode(() -> notificador.notificarReembolso(inscricao, participante, reembolso))
                .doesNotThrowAnyException();
    }

    private static Inscricao inscricao() {
        return new Inscricao("evento-1", "participante-1", BigDecimal.ZERO);
    }

    private static Participante participante() {
        return new Participante(
                "12345",
                "Maria",
                "maria@email.com",
                "11999999999",
                LocalDate.of(1995, 1, 1)
        );
    }

    private static Reembolso reembolso() {
        return Reembolso.criar(
                "inscricao-1",
                "participante-1",
                new BigDecimal("80.00"),
                DestinoReembolso.CARTEIRA,
                MotivoReembolso.SOLICITADO_PARTICIPANTE
        );
    }
}

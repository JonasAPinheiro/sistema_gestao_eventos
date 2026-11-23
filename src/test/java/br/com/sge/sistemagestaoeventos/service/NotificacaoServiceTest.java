package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.enums.CanalNotificacao;
import br.com.sge.sistemagestaoeventos.enums.StatusInscricao;
import br.com.sge.sistemagestaoeventos.enums.StatusNotificacao;
import br.com.sge.sistemagestaoeventos.enums.DestinoReembolso;
import br.com.sge.sistemagestaoeventos.enums.MotivoReembolso;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.model.notificacao.Notificacao;
import br.com.sge.sistemagestaoeventos.model.reembolso.Reembolso;
import br.com.sge.sistemagestaoeventos.notificacao.Notificador;
import br.com.sge.sistemagestaoeventos.repository.NotificacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock
    private Notificador email;

    @Mock
    private Notificador sms;

    @Mock
    private NotificacaoRepository notificacaoRepository;

    private NotificacaoService notificacaoService;

    @BeforeEach
    void setUp() {
        notificacaoService = new NotificacaoService(List.of(email, sms), notificacaoRepository);
    }

    @Test
    void deveNotificarTodosOsCanaisERegistrarEnvios() {
        Inscricao inscricao = inscricaoConfirmada();
        Participante participante = participante();
        prepararCanais();

        notificacaoService.notificarAlteracaoStatus(inscricao, participante);

        verify(email).notificar(inscricao, participante);
        verify(sms).notificar(inscricao, participante);
        ArgumentCaptor<Notificacao> captor = ArgumentCaptor.forClass(Notificacao.class);
        verify(notificacaoRepository, times(2)).salvar(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(Notificacao::status)
                .containsOnly(StatusNotificacao.ENVIADA);
    }

    @Test
    void deveRegistrarFalhaDoSmsSemInterromperNotificacoes() {
        Inscricao inscricao = inscricaoConfirmada();
        Participante participante = participante();
        prepararCanais();
        doThrow(new IllegalStateException("Gateway indisponível"))
                .when(sms).notificar(inscricao, participante);

        notificacaoService.notificarAlteracaoStatus(inscricao, participante);

        verify(email).notificar(inscricao, participante);
        verify(sms).notificar(inscricao, participante);
        ArgumentCaptor<Notificacao> captor = ArgumentCaptor.forClass(Notificacao.class);
        verify(notificacaoRepository, times(2)).salvar(captor.capture());
        assertThat(captor.getAllValues())
                .anySatisfy(notificacao -> {
                    assertThat(notificacao.canal()).isEqualTo(CanalNotificacao.SMS);
                    assertThat(notificacao.status()).isEqualTo(StatusNotificacao.FALHA);
                    assertThat(notificacao.erro()).isEqualTo("Gateway indisponível");
                });
        assertThat(inscricao.getStatus()).isEqualTo(StatusInscricao.CONFIRMADA);
    }

    @Test
    void deveNotificarReembolsoEmTodosOsCanaisERegistrarEnvios() {
        Inscricao inscricao = inscricaoConfirmada();
        Participante participante = participante();
        Reembolso reembolso = Reembolso.criar(inscricao.getId(), participante.getId(), new BigDecimal("80.00"),
                DestinoReembolso.CARTEIRA, MotivoReembolso.SOLICITADO_PARTICIPANTE);
        prepararCanais();

        notificacaoService.notificarReembolso(inscricao, participante, reembolso);

        verify(email).notificarReembolso(inscricao, participante, reembolso);
        verify(sms).notificarReembolso(inscricao, participante, reembolso);
        ArgumentCaptor<Notificacao> captor = ArgumentCaptor.forClass(Notificacao.class);
        verify(notificacaoRepository, times(2)).salvar(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(Notificacao::status)
                .containsOnly(StatusNotificacao.ENVIADA);
        assertThat(captor.getAllValues())
                .extracting(Notificacao::mensagem)
                .allMatch(mensagem -> mensagem.contains("80.00") && mensagem.contains("CARTEIRA"));
    }

    @Test
    void deveListarTodasAsNotificacoesDoRepositorio() {
        List<Notificacao> notificacoes = List.of(Notificacao.criar(
                "inscricao-1", "participante-1", "maria@email.com", CanalNotificacao.EMAIL,
                StatusInscricao.CONFIRMADA, "Sua inscrição foi CONFIRMADA.", StatusNotificacao.ENVIADA, null));
        when(notificacaoRepository.listarTodas()).thenReturn(notificacoes);

        List<Notificacao> resultado = notificacaoService.listarTodas();

        assertThat(resultado).isEqualTo(notificacoes);
        verify(notificacaoRepository).listarTodas();
    }

    private static Inscricao inscricaoConfirmada() {
        Inscricao inscricao = new Inscricao("evento-1", "participante-1", BigDecimal.ZERO);
        inscricao.confirmar();
        return inscricao;
    }

    private void prepararCanais() {
        when(email.canal()).thenReturn(CanalNotificacao.EMAIL);
        when(sms.canal()).thenReturn(CanalNotificacao.SMS);
    }

    private static Participante participante() {
        return new Participante("12345", "Maria", "maria@email.com", "11999999999", LocalDate.of(1995, 1, 1));
    }
}

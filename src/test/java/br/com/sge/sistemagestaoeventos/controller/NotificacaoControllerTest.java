package br.com.sge.sistemagestaoeventos.controller;

import br.com.sge.sistemagestaoeventos.enums.CanalNotificacao;
import br.com.sge.sistemagestaoeventos.enums.StatusInscricao;
import br.com.sge.sistemagestaoeventos.enums.StatusNotificacao;
import br.com.sge.sistemagestaoeventos.model.notificacao.Notificacao;
import br.com.sge.sistemagestaoeventos.service.NotificacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificacaoController.class)
class NotificacaoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificacaoService notificacaoService;

    @Test
    void deveListarHistoricoDeNotificacoes() throws Exception {
        Notificacao notificacao = new Notificacao("notificacao-1", "inscricao-1", "participante-1", "maria@email.com",
                CanalNotificacao.EMAIL, StatusInscricao.CONFIRMADA, "Sua inscrição foi CONFIRMADA.",
                StatusNotificacao.ENVIADA, null, LocalDateTime.now());
        when(notificacaoService.listarTodas()).thenReturn(List.of(notificacao));

        mockMvc.perform(get("/notificacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("notificacao-1"))
                .andExpect(jsonPath("$[0].canal").value("EMAIL"))
                .andExpect(jsonPath("$[0].status").value("ENVIADA"));

        verify(notificacaoService).listarTodas();
    }
}

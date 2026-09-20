package br.com.sge.sistemagestaoeventos.controller;

import br.com.sge.sistemagestaoeventos.dto.InscricaoRequestDTO;
import br.com.sge.sistemagestaoeventos.exception.InscricaoNaoEncontradaException;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.service.InscricaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InscricaoController.class)
class InscricaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InscricaoService inscricaoService;

    @Test
    @DisplayName("POST /eventos/{eventoId}/inscricoes - Deve retornar 201 ao realizar inscrição")
    void deveRealizarInscricao() throws Exception {
        Inscricao inscricao = criarInscricao("evento-1", "participante-1");
        InscricaoRequestDTO dto = new InscricaoRequestDTO("participante-1");
        when(inscricaoService.inscrever("evento-1", "participante-1")).thenReturn(inscricao);

        mockMvc.perform(post("/eventos/evento-1/inscricoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventoId").value("evento-1"))
                .andExpect(jsonPath("$.participanteId").value("participante-1"))
                .andExpect(jsonPath("$.status").value("CONFIRMADA"));

        verify(inscricaoService).inscrever("evento-1", "participante-1");
    }

    @Test
    @DisplayName("DELETE /eventos/{eventoId}/inscricoes/{participanteId} - Deve retornar 204 ao cancelar inscrição")
    void deveCancelarInscricao() throws Exception {
        mockMvc.perform(delete("/eventos/evento-1/inscricoes/participante-1"))
                .andExpect(status().isNoContent());

        verify(inscricaoService).cancelar("evento-1", "participante-1");
    }

    @Test
    @DisplayName("GET /eventos/{eventoId}/inscricoes - Deve retornar inscrições do evento")
    void deveListarInscricoesPorEvento() throws Exception {
        when(inscricaoService.listarPorEvento("evento-1"))
                .thenReturn(List.of(criarInscricao("evento-1", "participante-1")));

        mockMvc.perform(get("/eventos/evento-1/inscricoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].eventoId").value("evento-1"))
                .andExpect(jsonPath("$[0].participanteId").value("participante-1"));

        verify(inscricaoService).listarPorEvento("evento-1");
    }

    @Test
    @DisplayName("GET /participantes/{participanteId}/inscricoes - Deve retornar inscrições do participante")
    void deveListarInscricoesPorParticipante() throws Exception {
        when(inscricaoService.listarPorParticipante("participante-1"))
                .thenReturn(List.of(criarInscricao("evento-1", "participante-1")));

        mockMvc.perform(get("/participantes/participante-1/inscricoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].participanteId").value("participante-1"));

        verify(inscricaoService).listarPorParticipante("participante-1");
    }

    @Test
    @DisplayName("GET /eventos/{eventoId}/inscricoes/{participanteId} - Deve retornar inscrição ativa")
    void deveConsultarInscricaoAtiva() throws Exception {
        when(inscricaoService.consultar("evento-1", "participante-1"))
                .thenReturn(criarInscricao("evento-1", "participante-1"));

        mockMvc.perform(get("/eventos/evento-1/inscricoes/participante-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventoId").value("evento-1"))
                .andExpect(jsonPath("$.participanteId").value("participante-1"));

        verify(inscricaoService).consultar("evento-1", "participante-1");
    }

    @Test
    @DisplayName("GET /eventos/{eventoId}/inscricoes/{participanteId} - Deve retornar 404 quando a inscrição não existir")
    void deveRetornar404QuandoInscricaoNaoExistir() throws Exception {
        when(inscricaoService.consultar("evento-1", "participante-1"))
                .thenThrow(new InscricaoNaoEncontradaException("evento-1", "participante-1"));

        mockMvc.perform(get("/eventos/evento-1/inscricoes/participante-1"))
                .andExpect(status().isNotFound());

        verify(inscricaoService).consultar("evento-1", "participante-1");
    }

    private static Inscricao criarInscricao(String eventoId, String participanteId) {
        return new Inscricao(eventoId, participanteId);
    }
}

package br.com.sge.sistemagestaoeventos.controller;

import br.com.sge.sistemagestaoeventos.dto.InscricaoRequestDTO;
import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.exception.InscricaoNaoEncontradaException;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.comprovante.Comprovante;
import br.com.sge.sistemagestaoeventos.service.ComprovanteService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InscricaoController.class)
class InscricaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InscricaoService inscricaoService;

    @MockitoBean
    private ComprovanteService comprovanteService;

    @Test
    @DisplayName("POST /eventos/{eventoId}/inscricoes - Deve retornar 201 com comprovante")
    void deveRealizarInscricaoComComprovante() throws Exception {

        Inscricao inscricao = criarInscricao(
                "evento-1",
                "participante-1"
        );

        Comprovante comprovante = criarComprovante(inscricao);

        InscricaoRequestDTO dto =
                new InscricaoRequestDTO("participante-1");

        when(inscricaoService.inscrever(
                "evento-1",
                "participante-1"
        )).thenReturn(inscricao);

        when(comprovanteService.emitir(inscricao))
                .thenReturn(comprovante);

        mockMvc.perform(
                        post("/eventos/evento-1/inscricoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventoId")
                        .value("evento-1"))
                .andExpect(jsonPath("$.participanteId")
                        .value("participante-1"))
                .andExpect(jsonPath("$.status")
                        .value("CONFIRMADA"))
                .andExpect(jsonPath("$.comprovante")
                        .exists())
                .andExpect(jsonPath("$.comprovante.inscricaoId")
                        .value(inscricao.getId()))
                .andExpect(jsonPath("$.comprovante.tipo")
                        .value("SIMPLES"))
                .andExpect(jsonPath("$.comprovante.conteudo")
                        .value("COMPROVANTE DE INSCRIÇÃO"));

        assertThat(inscricao.getComprovante())
                .isSameAs(comprovante);

        verify(inscricaoService)
                .inscrever(
                        "evento-1",
                        "participante-1"
                );

        verify(comprovanteService)
                .emitir(inscricao);
    }

    @Test
    @DisplayName("DELETE /eventos/{eventoId}/inscricoes/{participanteId} - Deve retornar 204")
    void deveCancelarInscricao() throws Exception {

        mockMvc.perform(
                        delete(
                                "/eventos/evento-1/inscricoes/participante-1"
                        )
                )
                .andExpect(status().isNoContent());

        verify(inscricaoService)
                .cancelar(
                        "evento-1",
                        "participante-1"
                );
    }

    @Test
    @DisplayName("GET /eventos/{eventoId}/inscricoes - Deve retornar inscrições com comprovante")
    void deveListarInscricoesPorEvento() throws Exception {

        Inscricao inscricao = criarInscricaoComComprovante(
                "evento-1",
                "participante-1"
        );

        when(inscricaoService.listarPorEvento("evento-1"))
                .thenReturn(List.of(inscricao));

        mockMvc.perform(
                        get("/eventos/evento-1/inscricoes")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].eventoId")
                        .value("evento-1"))
                .andExpect(jsonPath("$[0].participanteId")
                        .value("participante-1"))
                .andExpect(jsonPath("$[0].comprovante")
                        .exists())
                .andExpect(jsonPath("$[0].comprovante.tipo")
                        .value("SIMPLES"))
                .andExpect(jsonPath("$[0].comprovante.conteudo")
                        .value("COMPROVANTE DE INSCRIÇÃO"));

        verify(inscricaoService)
                .listarPorEvento("evento-1");

        verifyNoInteractions(comprovanteService);
    }

    @Test
    @DisplayName("GET /participantes/{participanteId}/inscricoes - Deve retornar inscrições com comprovante")
    void deveListarInscricoesPorParticipante() throws Exception {

        Inscricao inscricao = criarInscricaoComComprovante(
                "evento-1",
                "participante-1"
        );

        when(inscricaoService.listarPorParticipante("participante-1"))
                .thenReturn(List.of(inscricao));

        mockMvc.perform(
                        get(
                                "/participantes/participante-1/inscricoes"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].participanteId")
                        .value("participante-1"))
                .andExpect(jsonPath("$[0].comprovante")
                        .exists())
                .andExpect(jsonPath("$[0].comprovante.tipo")
                        .value("SIMPLES"));

        verify(inscricaoService)
                .listarPorParticipante("participante-1");

        verifyNoInteractions(comprovanteService);
    }

    @Test
    @DisplayName("GET /eventos/{eventoId}/inscricoes/{participanteId} - Deve retornar inscrição com comprovante")
    void deveConsultarInscricaoAtiva() throws Exception {

        Inscricao inscricao = criarInscricaoComComprovante(
                "evento-1",
                "participante-1"
        );

        when(inscricaoService.consultar(
                "evento-1",
                "participante-1"
        )).thenReturn(inscricao);

        mockMvc.perform(
                        get(
                                "/eventos/evento-1/inscricoes/participante-1"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventoId")
                        .value("evento-1"))
                .andExpect(jsonPath("$.participanteId")
                        .value("participante-1"))
                .andExpect(jsonPath("$.comprovante")
                        .exists())
                .andExpect(jsonPath("$.comprovante.tipo")
                        .value("SIMPLES"))
                .andExpect(jsonPath("$.comprovante.conteudo")
                        .value("COMPROVANTE DE INSCRIÇÃO"));

        verify(inscricaoService)
                .consultar(
                        "evento-1",
                        "participante-1"
                );

        verifyNoInteractions(comprovanteService);
    }

    @Test
    @DisplayName("GET /eventos/{eventoId}/inscricoes/{participanteId} - Deve retornar 404 quando a inscrição não existir")
    void deveRetornar404QuandoInscricaoNaoExistir()
            throws Exception {

        when(inscricaoService.consultar(
                "evento-1",
                "participante-1"
        )).thenThrow(
                new InscricaoNaoEncontradaException(
                        "evento-1",
                        "participante-1"
                )
        );

        mockMvc.perform(
                        get(
                                "/eventos/evento-1/inscricoes/participante-1"
                        )
                )
                .andExpect(status().isNotFound());

        verify(inscricaoService)
                .consultar(
                        "evento-1",
                        "participante-1"
                );

        verifyNoInteractions(comprovanteService);
    }

    private static Inscricao criarInscricao(
            String eventoId,
            String participanteId
    ) {
        return new Inscricao(
                eventoId,
                participanteId
        );
    }

    private static Inscricao criarInscricaoComComprovante(
            String eventoId,
            String participanteId
    ) {
        Inscricao inscricao = criarInscricao(
                eventoId,
                participanteId
        );

        inscricao.setComprovante(
                criarComprovante(inscricao)
        );

        return inscricao;
    }

    private static Comprovante criarComprovante(
            Inscricao inscricao
    ) {
        return new Comprovante(
                inscricao.getId(),
                TipoComprovante.SIMPLES,
                "COMPROVANTE DE INSCRIÇÃO"
        );
    }
}

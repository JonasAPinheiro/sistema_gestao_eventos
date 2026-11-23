
package br.com.sge.sistemagestaoeventos.controller;

import br.com.sge.sistemagestaoeventos.enums.DestinoReembolso;
import br.com.sge.sistemagestaoeventos.enums.MotivoReembolso;
import br.com.sge.sistemagestaoeventos.model.carteira.Carteira;
import br.com.sge.sistemagestaoeventos.model.reembolso.Reembolso;
import br.com.sge.sistemagestaoeventos.service.CarteiraService;
import br.com.sge.sistemagestaoeventos.service.ReembolsoService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReembolsoController.class)
class ReembolsoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReembolsoService reembolsoService;

    @MockitoBean
    private CarteiraService carteiraService;

    @Test
    @DisplayName("POST /eventos/{eventoId}/inscricoes/{participanteId}/reembolso - Deve retornar 201 ao solicitar reembolso")
    void deveSolicitarReembolso() throws Exception {
        String eventoId = "evento-1";
        String participanteId = "participante-1";

        MotivoReembolso motivo = MotivoReembolso.values()[0];

        Reembolso reembolso = new Reembolso(
                "reembolso-1",
                "inscricao-1",
                participanteId,
                new BigDecimal("150.00"),
                DestinoReembolso.CARTEIRA,
                motivo,
                LocalDateTime.of(2026, 11, 22, 19, 0)
        );

        when(reembolsoService.solicitar(
                eventoId,
                participanteId,
                DestinoReembolso.CARTEIRA
        )).thenReturn(reembolso);

        var dto = new br.com.sge.sistemagestaoeventos.dto.ReembolsoRequestDTO(
                DestinoReembolso.CARTEIRA
        );

        mockMvc.perform(post(
                        "/eventos/{eventoId}/inscricoes/{participanteId}/reembolso",
                        eventoId,
                        participanteId
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("reembolso-1"))
                .andExpect(jsonPath("$.inscricaoId").value("inscricao-1"))
                .andExpect(jsonPath("$.participanteId").value(participanteId))
                .andExpect(jsonPath("$.valor").value(150.00))
                .andExpect(jsonPath("$.destino").value("CARTEIRA"))
                .andExpect(jsonPath("$.motivo").value(motivo.name()))
                .andExpect(jsonPath("$.criadoEm").exists());

        verify(reembolsoService).solicitar(
                eventoId,
                participanteId,
                DestinoReembolso.CARTEIRA
        );
    }

    @Test
    @DisplayName("GET /participantes/{participanteId}/carteira - Deve retornar 200 e os dados da carteira")
    void deveConsultarCarteira() throws Exception {
        String participanteId = "participante-1";

        Carteira carteira = mock(Carteira.class);

        when(carteiraService.consultar(participanteId))
                .thenReturn(carteira);

        when(carteira.getParticipanteId())
                .thenReturn(participanteId);

        when(carteira.getSaldo())
                .thenReturn(new BigDecimal("250.00"));

        mockMvc.perform(get("/participantes/{participanteId}/carteira", participanteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participanteId").value(participanteId))
                .andExpect(jsonPath("$.saldo").value(250.00));

        verify(carteiraService).consultar(participanteId);
    }
}

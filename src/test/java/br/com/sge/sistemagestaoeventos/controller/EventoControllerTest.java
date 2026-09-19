package br.com.sge.sistemagestaoeventos.controller;

import br.com.sge.sistemagestaoeventos.dto.EventoRequestDTO;
import br.com.sge.sistemagestaoeventos.exception.EventoNaoEncontradoException;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.service.EventoService;
import br.com.sge.sistemagestaoeventos.service.InscricaoService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EventoController.class)
class EventoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventoService eventoService;

    @MockitoBean
    private InscricaoService inscricaoService;
    @Test
    @DisplayName("GET /eventos/{id} - Deve retornar status 200 e o evento quando o ID existir")
    void deveBuscarPorIdExistente() throws Exception {
        Evento evento = criarEvento("Evento de Teste");

        when(eventoService.buscarPorId("1"))
                .thenReturn(evento);

        mockMvc.perform(get("/eventos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Evento de Teste"))
                .andExpect(jsonPath("$.descricao").value("Descrição do evento"))
                .andExpect(jsonPath("$.local").value("Centro de Eventos"))
                .andExpect(jsonPath("$.capacidadeMaxima").value(100));

        verify(eventoService).buscarPorId("1");
    }

    @Test
    @DisplayName("GET /eventos/{id} - Deve retornar 404 quando o evento não existir")
    void deveRetornar404QuandoEventoNaoExistir() throws Exception {
        when(eventoService.buscarPorId("99"))
                .thenThrow(new EventoNaoEncontradoException("99"));

        mockMvc.perform(get("/eventos/99"))
                .andExpect(status().isNotFound());

        verify(eventoService).buscarPorId("99");
    }

    @Test
    @DisplayName("GET /eventos - Deve retornar status 200 e lista de eventos")
    void deveListarTodos() throws Exception {
        List<Evento> eventos = List.of(
                criarEvento("Evento de Tecnologia"),
                criarEvento("Evento de Java")
        );

        when(eventoService.listarTodos()).thenReturn(eventos);

        mockMvc.perform(get("/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].titulo").value("Evento de Tecnologia"))
                .andExpect(jsonPath("$[1].titulo").value("Evento de Java"));

        verify(eventoService).listarTodos();
    }

    @Test
    @DisplayName("POST /eventos - Deve retornar 201 Created ao cadastrar evento")
    void deveCadastrarEvento() throws Exception {
        Evento eventoCriado = criarEvento("Novo Evento");

        EventoRequestDTO dto = new EventoRequestDTO(
                "Novo Evento",
                "Descrição do evento",
                LocalDate.now().plusDays(10),
                LocalTime.of(18, 0),
                LocalTime.of(20, 0),
                "Centro de Eventos",
                100
        );

        when(eventoService.cadastrar(any(Evento.class)))
                .thenReturn(eventoCriado);

        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Novo Evento"))
                .andExpect(jsonPath("$.descricao").value("Descrição do evento"))
                .andExpect(jsonPath("$.local").value("Centro de Eventos"))
                .andExpect(jsonPath("$.capacidadeMaxima").value(100));

        verify(eventoService).cadastrar(any(Evento.class));
    }

    @Test
    @DisplayName("PUT /eventos/{id} - Deve retornar status 200 ao atualizar evento")
    void deveAtualizarEvento() throws Exception {
        Evento eventoAtualizado = criarEvento("Evento Atualizado");

        EventoRequestDTO dto = new EventoRequestDTO(
                "Evento Atualizado",
                "Nova descrição",
                LocalDate.now().plusDays(15),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0),
                "Novo Local",
                200
        );

        when(eventoService.atualizar(eq("1"), any(Evento.class)))
                .thenReturn(eventoAtualizado);

        mockMvc.perform(put("/eventos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Evento Atualizado"))
                .andExpect(jsonPath("$.capacidadeMaxima").value(100));

        verify(eventoService).atualizar(eq("1"), any(Evento.class));
    }

    @Test
    @DisplayName("PATCH /eventos/{id}/cancelamento - Deve retornar status 200 ao cancelar evento")
    void deveCancelarEvento() throws Exception {
        Evento eventoCancelado = criarEvento("Evento Cancelado");
        eventoCancelado.cancelar();

        when(eventoService.cancelar("1"))
                .thenReturn(eventoCancelado);

        mockMvc.perform(patch("/eventos/1/cancelamento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Evento Cancelado"))
                .andExpect(jsonPath("$.status").value("CANCELADO"));

        verify(eventoService).cancelar("1");
    }

    @Test
    @DisplayName("GET /eventos/{id}/vagas - Deve retornar quantidade de vagas")
    void deveConsultarVagas() throws Exception {
        Evento evento = criarEvento("Evento com Vagas");

        when(eventoService.buscarPorId("1"))
                .thenReturn(evento);

        when(inscricaoService.contarConfirmadas("1"))
                .thenReturn(30L);

        mockMvc.perform(get("/eventos/1/vagas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacidadeMaxima").value(100))
                .andExpect(jsonPath("$.inscricoesConfirmadas").value(30))
                .andExpect(jsonPath("$.vagasDisponiveis").value(70));

        verify(eventoService).buscarPorId("1");
        verify(inscricaoService).contarConfirmadas("1");
    }

    private static Evento criarEvento(String titulo) {
        return new Evento(
                titulo,
                "Descrição do evento",
                LocalDate.now().plusDays(10),
                LocalTime.of(18, 0),
                LocalTime.of(20, 0),
                "Centro de Eventos",
                100
        );
    }
}
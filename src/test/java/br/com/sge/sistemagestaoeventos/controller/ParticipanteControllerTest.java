package br.com.sge.sistemagestaoeventos.controller;

import br.com.sge.sistemagestaoeventos.dto.ParticipanteRequestDTO;
import br.com.sge.sistemagestaoeventos.exception.ParticipanteNaoEncontradoException;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.service.ParticipanteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ParticipanteController.class)
class ParticipanteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ParticipanteService participanteService;

    @Test
    @DisplayName("GET /participantes/{id} - Deve retornar status 200 e o participante quando o ID existir")
    void deveBuscarPorIdExistente() throws Exception {
        Participante participante = criarParticipante("Maria Silva", "maria@email.com");

        when(participanteService.buscarPorId("1")).thenReturn(participante);

        mockMvc.perform(get("/participantes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(participante.getId()))
                .andExpect(jsonPath("$.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.email").value("maria@email.com"))
                .andExpect(jsonPath("$.criadoEm").exists());

        verify(participanteService).buscarPorId("1");
    }

    @Test
    @DisplayName("GET /participantes/{id} - Deve retornar 404 quando o participante não existir")
    void deveRetornar404QuandoParticipanteNaoExistir() throws Exception {
        when(participanteService.buscarPorId("99"))
                .thenThrow(new ParticipanteNaoEncontradoException("99"));

        mockMvc.perform(get("/participantes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Participante não encontrado com o id: 99"))
                .andExpect(jsonPath("$.path").value("/participantes/99"));

        verify(participanteService).buscarPorId("99");
    }

    @Test
    @DisplayName("GET /participantes - Deve retornar status 200 e lista de participantes")
    void deveListarTodos() throws Exception {
        List<Participante> participantes = List.of(
                criarParticipante("Maria Silva", "maria@email.com"),
                criarParticipante("João Souza", "joao@email.com")
        );

        when(participanteService.listarTodos()).thenReturn(participantes);

        mockMvc.perform(get("/participantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Maria Silva"))
                .andExpect(jsonPath("$[0].email").value("maria@email.com"))
                .andExpect(jsonPath("$[1].nome").value("João Souza"))
                .andExpect(jsonPath("$[1].email").value("joao@email.com"));

        verify(participanteService).listarTodos();
    }

    @Test
    @DisplayName("POST /participantes - Deve retornar 201 Created ao cadastrar participante")
    void deveCadastrarParticipante() throws Exception {
        Participante participanteCriado = criarParticipante("Maria Silva", "maria@email.com");
        ParticipanteRequestDTO dto = new ParticipanteRequestDTO("Maria Silva", "maria@email.com");

        when(participanteService.cadastrar(any(Participante.class)))
                .thenReturn(participanteCriado);

        mockMvc.perform(post("/participantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(participanteCriado.getId()))
                .andExpect(jsonPath("$.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.email").value("maria@email.com"));

        verify(participanteService).cadastrar(argThat(participante ->
                participante.getNome().equals("Maria Silva")
                        && participante.getEmail().equals("maria@email.com")));
    }

    @Test
    @DisplayName("PUT /participantes/{id} - Deve retornar status 200 ao atualizar participante")
    void deveAtualizarParticipante() throws Exception {
        Participante participanteAtualizado = criarParticipante("Maria Souza", "maria.souza@email.com");
        ParticipanteRequestDTO dto = new ParticipanteRequestDTO("Maria Souza", "maria.souza@email.com");

        when(participanteService.atualizar(eq("1"), any(Participante.class)))
                .thenReturn(participanteAtualizado);

        mockMvc.perform(put("/participantes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Souza"))
                .andExpect(jsonPath("$.email").value("maria.souza@email.com"));

        verify(participanteService).atualizar(eq("1"), argThat(participante ->
                participante.getNome().equals("Maria Souza")
                        && participante.getEmail().equals("maria.souza@email.com")));
    }

    @Test
    @DisplayName("POST /participantes - Deve retornar 400 quando o e-mail já estiver cadastrado")
    void deveRetornar400QuandoEmailJaEstiverCadastrado() throws Exception {
        ParticipanteRequestDTO dto = new ParticipanteRequestDTO("Maria Silva", "maria@email.com");

        when(participanteService.cadastrar(any(Participante.class)))
                .thenThrow(new RegraNegocioException("Já existe um participante cadastrado com este e-mail."));

        mockMvc.perform(post("/participantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Já existe um participante cadastrado com este e-mail."))
                .andExpect(jsonPath("$.path").value("/participantes"));

        verify(participanteService).cadastrar(any(Participante.class));
    }

    private static Participante criarParticipante(String nome, String email) {
        return new Participante(nome, email);
    }
}

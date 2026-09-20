package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.exception.ParticipanteNaoEncontradoException;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.repository.ParticipanteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParticipanteServiceTest {

    @Mock
    private ParticipanteRepository participanteRepository;

    @InjectMocks
    private ParticipanteService participanteService;

    @Nested
    @DisplayName("Testes de Consulta")
    class Consultas {

        @Test
        @DisplayName("Deve buscar participante por ID existente")
        void deveBuscarPorIdExistente() {
            Participante participante = criarParticipante("Maria Silva", "maria@email.com");

            when(participanteRepository.buscarPorId("1")).thenReturn(Optional.of(participante));

            Participante resultado = participanteService.buscarPorId("1");

            assertThat(resultado).isEqualTo(participante);
            verify(participanteRepository).buscarPorId("1");
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar ID inexistente")
        void deveLancarExcecaoQuandoIdNaoEncontrado() {
            when(participanteRepository.buscarPorId("99")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> participanteService.buscarPorId("99"))
                    .isInstanceOf(ParticipanteNaoEncontradoException.class)
                    .hasMessage("Participante não encontrado com o id: 99");

            verify(participanteRepository).buscarPorId("99");
        }

        @Test
        @DisplayName("Deve listar todos os participantes")
        void deveListarTodos() {
            List<Participante> participantes = List.of(
                    criarParticipante("Maria Silva", "maria@email.com"),
                    criarParticipante("João Souza", "joao@email.com")
            );

            when(participanteRepository.listarTodos()).thenReturn(participantes);

            List<Participante> resultado = participanteService.listarTodos();

            assertThat(resultado).hasSize(2).isEqualTo(participantes);
            verify(participanteRepository).listarTodos();
        }
    }

    @Nested
    @DisplayName("Testes de Regras de Negócio e Alteração")
    class Negocio {

        @Test
        @DisplayName("Deve cadastrar participante válido")
        void deveCadastrarParticipante() {
            Participante participante = criarParticipante("Maria Silva", "maria@email.com");

            when(participanteRepository.buscarPorEmail("maria@email.com"))
                    .thenReturn(Optional.empty());
            when(participanteRepository.salvar(participante)).thenReturn(participante);

            Participante resultado = participanteService.cadastrar(participante);

            assertThat(resultado).isEqualTo(participante);
            verify(participanteRepository).buscarPorEmail("maria@email.com");
            verify(participanteRepository).salvar(participante);
        }

        @Test
        @DisplayName("Deve atualizar participante existente")
        void deveAtualizarParticipante() {
            Participante existente = criarParticipante("Maria Silva", "maria@email.com");
            Participante dadosAtualizados = criarParticipante("Maria Souza", "maria.souza@email.com");

            when(participanteRepository.buscarPorId(existente.getId()))
                    .thenReturn(Optional.of(existente));
            when(participanteRepository.buscarPorEmail("maria.souza@email.com"))
                    .thenReturn(Optional.empty());
            when(participanteRepository.salvar(existente)).thenReturn(existente);

            Participante resultado = participanteService.atualizar(existente.getId(), dadosAtualizados);

            assertThat(resultado).isSameAs(existente);
            assertThat(resultado.getNome()).isEqualTo("Maria Souza");
            assertThat(resultado.getEmail()).isEqualTo("maria.souza@email.com");
            verify(participanteRepository).buscarPorId(existente.getId());
            verify(participanteRepository).buscarPorEmail("maria.souza@email.com");
            verify(participanteRepository).salvar(existente);
        }

        @Test
        @DisplayName("Deve permitir atualização mantendo o próprio e-mail")
        void deveAtualizarMantendoProprioEmail() {
            Participante existente = criarParticipante("Maria Silva", "maria@email.com");
            Participante dadosAtualizados = criarParticipante("Maria Souza", "maria@email.com");

            when(participanteRepository.buscarPorId(existente.getId()))
                    .thenReturn(Optional.of(existente));
            when(participanteRepository.buscarPorEmail("maria@email.com"))
                    .thenReturn(Optional.of(existente));
            when(participanteRepository.salvar(existente)).thenReturn(existente);

            Participante resultado = participanteService.atualizar(existente.getId(), dadosAtualizados);

            assertThat(resultado.getNome()).isEqualTo("Maria Souza");
            assertThat(resultado.getEmail()).isEqualTo("maria@email.com");
            verify(participanteRepository).salvar(existente);
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar participante inexistente")
        void deveLancarExcecaoAoAtualizarParticipanteInexistente() {
            Participante dadosAtualizados = criarParticipante("Maria Souza", "maria.souza@email.com");

            when(participanteRepository.buscarPorId("99")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> participanteService.atualizar("99", dadosAtualizados))
                    .isInstanceOf(ParticipanteNaoEncontradoException.class);

            verify(participanteRepository).buscarPorId("99");
            verify(participanteRepository, never()).salvar(any(Participante.class));
        }
    }

    @Nested
    @DisplayName("Testes de Validação")
    class Validacoes {

        @Test
        @DisplayName("Deve lançar exceção quando nome não for informado")
        void deveValidarNomeObrigatorio() {
            Participante participante = criarParticipante("", "maria@email.com");

            assertThatThrownBy(() -> participanteService.cadastrar(participante))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("O nome do participante é obrigatório.");

            verify(participanteRepository, never()).salvar(any(Participante.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando nome for nulo")
        void deveValidarNomeNulo() {
            Participante participante = criarParticipante(null, "maria@email.com");

            assertThatThrownBy(() -> participanteService.cadastrar(participante))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("O nome do participante é obrigatório.");

            verify(participanteRepository, never()).salvar(any(Participante.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando e-mail não for informado")
        void deveValidarEmailObrigatorio() {
            Participante participante = criarParticipante("Maria Silva", "");

            assertThatThrownBy(() -> participanteService.cadastrar(participante))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("O e-mail do participante é obrigatório.");

            verify(participanteRepository, never()).salvar(any(Participante.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando e-mail for nulo")
        void deveValidarEmailNulo() {
            Participante participante = criarParticipante("Maria Silva", null);

            assertThatThrownBy(() -> participanteService.cadastrar(participante))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("O e-mail do participante é obrigatório.");

            verify(participanteRepository, never()).salvar(any(Participante.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando e-mail possuir formato inválido")
        void deveValidarFormatoDoEmail() {
            Participante participante = criarParticipante("Maria Silva", "email-invalido");

            assertThatThrownBy(() -> participanteService.cadastrar(participante))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("O e-mail do participante deve possuir um formato válido.");

            verify(participanteRepository, never()).salvar(any(Participante.class));
        }

        @Test
        @DisplayName("Deve lançar exceção ao cadastrar e-mail já utilizado")
        void deveValidarEmailUnicoNoCadastro() {
            Participante participante = criarParticipante("Maria Silva", "maria@email.com");
            Participante existente = criarParticipante("Outra Pessoa", "maria@email.com");

            when(participanteRepository.buscarPorEmail("maria@email.com"))
                    .thenReturn(Optional.of(existente));

            assertThatThrownBy(() -> participanteService.cadastrar(participante))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("Já existe um participante cadastrado com este e-mail.");

            verify(participanteRepository, never()).salvar(any(Participante.class));
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar para e-mail de outro participante")
        void deveValidarEmailUnicoNaAtualizacao() {
            Participante participante = criarParticipante("Maria Silva", "maria@email.com");
            Participante outroParticipante = criarParticipante("João Souza", "joao@email.com");
            Participante dadosAtualizados = criarParticipante("Maria Silva", "joao@email.com");

            when(participanteRepository.buscarPorId(participante.getId()))
                    .thenReturn(Optional.of(participante));
            when(participanteRepository.buscarPorEmail("joao@email.com"))
                    .thenReturn(Optional.of(outroParticipante));

            assertThatThrownBy(() -> participanteService.atualizar(participante.getId(), dadosAtualizados))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("Já existe um participante cadastrado com este e-mail.");

            assertThat(participante.getNome()).isEqualTo("Maria Silva");
            assertThat(participante.getEmail()).isEqualTo("maria@email.com");
            verify(participanteRepository, never()).salvar(any(Participante.class));
        }
    }

    private static Participante criarParticipante(String nome, String email) {
        return new Participante(nome, email);
    }
}

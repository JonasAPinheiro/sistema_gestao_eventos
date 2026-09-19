package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.exception.EventoNaoEncontradoException;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.repository.EventoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private EventoService eventoService;

    @Nested
    @DisplayName("Testes de Consulta")
    class Consultas {

        @Test
        @DisplayName("Deve buscar evento por ID existente")
        void deveBuscarPorIdExistente() {
            Evento evento = criarEvento("Evento de Teste");

            when(eventoRepository.buscarPorId("1")).thenReturn(Optional.of(evento));

            Evento resultado = eventoService.buscarPorId("1");

            assertThat(resultado).isEqualTo(evento);

            verify(eventoRepository).buscarPorId("1");
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar ID inexistente")
        void deveLancarExcecaoQuandoIdNaoEncontrado() {
            when(eventoRepository.buscarPorId("99")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> eventoService.buscarPorId("99"))
                    .isInstanceOf(EventoNaoEncontradoException.class);

            verify(eventoRepository).buscarPorId("99");
        }

        @Test
        @DisplayName("Deve listar todos os eventos")
        void deveListarTodos() {
            List<Evento> eventos = List.of(
                    criarEvento("Evento 1"),
                    criarEvento("Evento 2")
            );

            when(eventoRepository.listarTodos()).thenReturn(eventos);

            List<Evento> resultado = eventoService.listarTodos();

            assertThat(resultado).hasSize(2).isEqualTo(eventos);

            verify(eventoRepository, times(1)).listarTodos();
        }
    }

    @Nested
    @DisplayName("Testes de Regras de Negócio e Alteração")
    class Negocio {

        @Test
        @DisplayName("Deve cadastrar evento válido")
        void deveCadastrarEvento() {
            Evento entrada = criarEvento("Novo Evento");
            Evento salvo = criarEvento("Novo Evento");

            when(eventoRepository.salvar(entrada))
                    .thenReturn(salvo);

            Evento resultado = eventoService.cadastrar(entrada);

            assertThat(resultado).isEqualTo(salvo);

            verify(eventoRepository).salvar(entrada);
        }

        @Test
        @DisplayName("Deve atualizar evento existente")
        void deveAtualizarEvento() {
            Evento existente = criarEvento("Evento Antigo");
            Evento dadosAtualizados = criarEvento("Evento Atualizado");

            when(eventoRepository.buscarPorId("1"))
                    .thenReturn(Optional.of(existente));

            when(eventoRepository.salvar(any(Evento.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Evento resultado = eventoService.atualizar("1", dadosAtualizados);

            assertThat(resultado.getTitulo())
                    .isEqualTo("Evento Atualizado");

            assertThat(resultado.getDescricao())
                    .isEqualTo(dadosAtualizados.getDescricao());

            assertThat(resultado.getData())
                    .isEqualTo(dadosAtualizados.getData());

            assertThat(resultado.getHoraInicio())
                    .isEqualTo(dadosAtualizados.getHoraInicio());

            assertThat(resultado.getHoraFim())
                    .isEqualTo(dadosAtualizados.getHoraFim());

            assertThat(resultado.getLocal())
                    .isEqualTo(dadosAtualizados.getLocal());

            assertThat(resultado.getCapacidadeMaxima())
                    .isEqualTo(dadosAtualizados.getCapacidadeMaxima());

            verify(eventoRepository).buscarPorId("1");
            verify(eventoRepository).salvar(existente);
        }

        @Test
        @DisplayName("Deve cancelar evento existente")
        void deveCancelarEvento() {
            Evento evento = criarEvento("Evento para Cancelar");

            when(eventoRepository.buscarPorId("1"))
                    .thenReturn(Optional.of(evento));

            when(eventoRepository.salvar(any(Evento.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Evento resultado = eventoService.cancelar("1");

            assertThat(resultado).isEqualTo(evento);

            verify(eventoRepository).buscarPorId("1");
            verify(eventoRepository).salvar(evento);
        }

        @Test
        @DisplayName("Deve lançar exceção ao cancelar evento inexistente")
        void deveLancarExcecaoAoCancelarEventoInexistente() {
            when(eventoRepository.buscarPorId("99"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> eventoService.cancelar("99"))
                    .isInstanceOf(EventoNaoEncontradoException.class);

            verify(eventoRepository).buscarPorId("99");
            verify(eventoRepository, never()).salvar(any(Evento.class));
        }
    }

    @Nested
    @DisplayName("Testes de Validação")
    class Validacoes {

        @Test
        @DisplayName("Deve lançar exceção quando título não for informado")
        void deveValidarTituloObrigatorio() {
            Evento evento = criarEvento("Evento Válido");
            evento.setTitulo("");

            assertThatThrownBy(() -> eventoService.cadastrar(evento))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("O título do evento é obrigatório.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando título for nulo")
        void deveValidarTituloNulo() {
            Evento evento = criarEvento("Evento Válido");
            evento.setTitulo(null);

            assertThatThrownBy(() -> eventoService.cadastrar(evento))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("O título do evento é obrigatório.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando descrição não for informada")
        void deveValidarDescricaoObrigatoria() {
            Evento evento = criarEvento("Evento Válido");
            evento.setDescricao("");

            assertThatThrownBy(() -> eventoService.cadastrar(evento))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("A descrição do evento é obrigatória.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando descrição for nula")
        void deveValidarDescricaoNula() {
            Evento evento = criarEvento("Evento Válido");
            evento.setDescricao(null);

            assertThatThrownBy(() -> eventoService.cadastrar(evento))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("A descrição do evento é obrigatória.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando data for anterior à atual")
        void deveValidarDataDoEvento() {
            Evento evento = criarEvento("Evento Válido");
            evento.setData(LocalDate.now().minusDays(1));

            assertThatThrownBy(() -> eventoService.cadastrar(evento))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("A data do evento não pode ser anterior à data atual.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando data for nula")
        void deveValidarDataNula() {
            Evento evento = criarEvento("Evento Válido");
            evento.setData(null);

            assertThatThrownBy(() -> eventoService.cadastrar(evento))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("A data do evento não pode ser anterior à data atual.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando hora de início for nula")
        void deveValidarHoraInicioNula() {
            Evento evento = criarEvento("Evento Válido");
            evento.setHoraInicio(null);

            assertThatThrownBy(() -> eventoService.cadastrar(evento))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("O horário de término deve ser posterior ao horário de início.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando hora de fim for nula")
        void deveValidarHoraFimNula() {
            Evento evento = criarEvento("Evento Válido");
            evento.setHoraFim(null);

            assertThatThrownBy(() -> eventoService.cadastrar(evento))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("O horário de término deve ser posterior ao horário de início.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando horário de término não for posterior ao início")
        void deveValidarHorarioDoEvento() {
            Evento evento = criarEvento("Evento Válido");
            evento.setHoraInicio(java.time.LocalTime.of(18, 0));
            evento.setHoraFim(java.time.LocalTime.of(18, 0));

            assertThatThrownBy(() -> eventoService.cadastrar(evento))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("O horário de término deve ser posterior ao horário de início.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando capacidade máxima for zero")
        void deveValidarCapacidadeMaxima() {
            Evento evento = criarEvento("Evento Válido");
            evento.setCapacidadeMaxima(0);

            assertThatThrownBy(() -> eventoService.cadastrar(evento))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("A capacidade máxima deve ser um número inteiro maior que zero.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando capacidade máxima for negativa")
        void deveRejeitarCapacidadeNegativa() {
            Evento evento = criarEvento("Evento Válido");
            evento.setCapacidadeMaxima(-10);

            assertThatThrownBy(() -> eventoService.cadastrar(evento))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("A capacidade máxima deve ser um número inteiro maior que zero.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }
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
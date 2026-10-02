package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.dto.EventoRequestDTO;
import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;
import br.com.sge.sistemagestaoeventos.exception.EventoNaoEncontradoException;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeAberta;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeEvento;
import br.com.sge.sistemagestaoeventos.factory.modalidade.ModalidadeEventoFactory;
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

    @Mock
    private ModalidadeEventoFactory modalidadeEventoFactory;

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
        @DisplayName("Deve cadastrar evento válido e criar a modalidade pela Factory")
        void deveCadastrarEvento() {
            EventoRequestDTO dto = criarDto("Novo Evento");
            ModalidadeEvento modalidade = new ModalidadeAberta();
            Evento eventoSalvo = dto.toEvento(modalidade);

            when(modalidadeEventoFactory.criar(TipoModalidade.ABERTO, null))
                    .thenReturn(modalidade);
            when(eventoRepository.salvar(any(Evento.class)))
                    .thenReturn(eventoSalvo);

            Evento resultado = eventoService.cadastrar(dto);

            assertThat(resultado).isEqualTo(eventoSalvo);
            assertThat(resultado.getModalidade()).isSameAs(modalidade);

            verify(modalidadeEventoFactory)
                    .criar(TipoModalidade.ABERTO, null);
            verify(eventoRepository).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve atualizar evento existente sem alterar a modalidade")
        void deveAtualizarEvento() {
            ModalidadeEvento modalidadeOriginal = new ModalidadeAberta();
            Evento existente = criarEvento("Evento Antigo", modalidadeOriginal);

            EventoRequestDTO dadosAtualizados = criarDto(
                    "Evento Atualizado",
                    "Nova descrição",
                    LocalDate.now().plusDays(15),
                    LocalTime.of(19, 0),
                    LocalTime.of(21, 0),
                    "Novo Local",
                    200,
                    TipoModalidade.RESTRICAO_IDADE,
                    18
            );

            when(eventoRepository.buscarPorId("1"))
                    .thenReturn(Optional.of(existente));

            when(eventoRepository.salvar(any(Evento.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Evento resultado = eventoService.atualizar("1", dadosAtualizados);

            assertThat(resultado.getTitulo())
                    .isEqualTo("Evento Atualizado");
            assertThat(resultado.getDescricao())
                    .isEqualTo("Nova descrição");
            assertThat(resultado.getData())
                    .isEqualTo(dadosAtualizados.data());
            assertThat(resultado.getHoraInicio())
                    .isEqualTo(dadosAtualizados.horaInicio());
            assertThat(resultado.getHoraFim())
                    .isEqualTo(dadosAtualizados.horaFim());
            assertThat(resultado.getLocal())
                    .isEqualTo(dadosAtualizados.local());
            assertThat(resultado.getCapacidadeMaxima())
                    .isEqualTo(dadosAtualizados.capacidadeMaxima());
            assertThat(resultado.getModalidade())
                    .isSameAs(modalidadeOriginal);

            verify(eventoRepository).buscarPorId("1");
            verify(eventoRepository).salvar(existente);
            verifyNoInteractions(modalidadeEventoFactory);
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
            EventoRequestDTO dto = criarDto("");

            assertThatThrownBy(() -> eventoService.cadastrar(dto))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("O título do evento é obrigatório.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando título for nulo")
        void deveValidarTituloNulo() {
            EventoRequestDTO dto = criarDto(null);

            assertThatThrownBy(() -> eventoService.cadastrar(dto))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("O título do evento é obrigatório.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando descrição não for informada")
        void deveValidarDescricaoObrigatoria() {
            EventoRequestDTO dto = criarDto(
                    "Evento Válido", "", LocalDate.now().plusDays(10),
                    LocalTime.of(18, 0), LocalTime.of(20, 0),
                    "Centro de Eventos", 100, TipoModalidade.ABERTO, null
            );

            assertThatThrownBy(() -> eventoService.cadastrar(dto))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("A descrição do evento é obrigatória.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando descrição for nula")
        void deveValidarDescricaoNula() {
            EventoRequestDTO dto = criarDto(
                    "Evento Válido", null, LocalDate.now().plusDays(10),
                    LocalTime.of(18, 0), LocalTime.of(20, 0),
                    "Centro de Eventos", 100, TipoModalidade.ABERTO, null
            );

            assertThatThrownBy(() -> eventoService.cadastrar(dto))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("A descrição do evento é obrigatória.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando data for anterior à atual")
        void deveValidarDataDoEvento() {
            EventoRequestDTO dto = criarDto(
                    "Evento Válido", "Descrição do evento", LocalDate.now().minusDays(1),
                    LocalTime.of(18, 0), LocalTime.of(20, 0),
                    "Centro de Eventos", 100, TipoModalidade.ABERTO, null
            );

            assertThatThrownBy(() -> eventoService.cadastrar(dto))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("A data do evento não pode ser anterior à data atual.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando data for nula")
        void deveValidarDataNula() {
            EventoRequestDTO dto = criarDto(
                    "Evento Válido", "Descrição do evento", null,
                    LocalTime.of(18, 0), LocalTime.of(20, 0),
                    "Centro de Eventos", 100, TipoModalidade.ABERTO, null
            );

            assertThatThrownBy(() -> eventoService.cadastrar(dto))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("A data do evento não pode ser anterior à data atual.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando hora de início for nula")
        void deveValidarHoraInicioNula() {
            EventoRequestDTO dto = criarDto(
                    "Evento Válido", "Descrição do evento", LocalDate.now().plusDays(10),
                    null, LocalTime.of(20, 0),
                    "Centro de Eventos", 100, TipoModalidade.ABERTO, null
            );

            assertThatThrownBy(() -> eventoService.cadastrar(dto))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("O horário de término deve ser posterior ao horário de início.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando hora de fim for nula")
        void deveValidarHoraFimNula() {
            EventoRequestDTO dto = criarDto(
                    "Evento Válido", "Descrição do evento", LocalDate.now().plusDays(10),
                    LocalTime.of(18, 0), null,
                    "Centro de Eventos", 100, TipoModalidade.ABERTO, null
            );

            assertThatThrownBy(() -> eventoService.cadastrar(dto))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("O horário de término deve ser posterior ao horário de início.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando horário de término não for posterior ao início")
        void deveValidarHorarioDoEvento() {
            EventoRequestDTO dto = criarDto(
                    "Evento Válido", "Descrição do evento", LocalDate.now().plusDays(10),
                    LocalTime.of(18, 0), LocalTime.of(18, 0),
                    "Centro de Eventos", 100, TipoModalidade.ABERTO, null
            );

            assertThatThrownBy(() -> eventoService.cadastrar(dto))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("O horário de término deve ser posterior ao horário de início.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando capacidade máxima for zero")
        void deveValidarCapacidadeMaxima() {
            EventoRequestDTO dto = criarDto(
                    "Evento Válido", "Descrição do evento", LocalDate.now().plusDays(10),
                    LocalTime.of(18, 0), LocalTime.of(20, 0),
                    "Centro de Eventos", 0, TipoModalidade.ABERTO, null
            );

            assertThatThrownBy(() -> eventoService.cadastrar(dto))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("A capacidade máxima deve ser um número inteiro maior que zero.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando capacidade máxima for negativa")
        void deveRejeitarCapacidadeNegativa() {
            EventoRequestDTO dto = criarDto(
                    "Evento Válido", "Descrição do evento", LocalDate.now().plusDays(10),
                    LocalTime.of(18, 0), LocalTime.of(20, 0),
                    "Centro de Eventos", -10, TipoModalidade.ABERTO, null
            );

            assertThatThrownBy(() -> eventoService.cadastrar(dto))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("A capacidade máxima deve ser um número inteiro maior que zero.");

            verify(eventoRepository, never()).salvar(any(Evento.class));
        }
    }

    private static Evento criarEvento(String titulo) {
        return criarEvento(titulo, new ModalidadeAberta());
    }

    private static Evento criarEvento(String titulo, ModalidadeEvento modalidade) {
        return new Evento(
                titulo,
                "Descrição do evento",
                LocalDate.now().plusDays(10),
                LocalTime.of(18, 0),
                LocalTime.of(20, 0),
                "Centro de Eventos",
                100,
                modalidade
        );
    }

    private static EventoRequestDTO criarDto(String titulo) {
        return criarDto(
                titulo,
                "Descrição do evento",
                LocalDate.now().plusDays(10),
                LocalTime.of(18, 0),
                LocalTime.of(20, 0),
                "Centro de Eventos",
                100,
                TipoModalidade.ABERTO,
                null
        );
    }

    private static EventoRequestDTO criarDto(
            String titulo,
            String descricao,
            LocalDate data,
            LocalTime horaInicio,
            LocalTime horaFim,
            String local,
            int capacidadeMaxima,
            TipoModalidade tipoModalidade,
            Integer idadeMinima
    ) {
        return new EventoRequestDTO(
                titulo,
                descricao,
                data,
                horaInicio,
                horaFim,
                local,
                capacidadeMaxima,
                tipoModalidade,
                idadeMinima
        );
    }
}

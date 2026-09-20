package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.enums.StatusInscricao;
import br.com.sge.sistemagestaoeventos.exception.InscricaoNaoEncontradaException;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.repository.InscricaoRepository;
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
class InscricaoServiceTest {

    @Mock
    private InscricaoRepository inscricaoRepository;

    @Mock
    private EventoService eventoService;

    @Mock
    private ParticipanteService participanteService;

    @InjectMocks
    private InscricaoService inscricaoService;

    @Nested
    @DisplayName("Testes de Consulta")
    class Consultas {

        @Test
        @DisplayName("Deve listar inscrições por evento")
        void deveListarInscricoesPorEvento() {
            Evento evento = criarEventoFuturo(100);
            List<Inscricao> inscricoes = List.of(criarInscricao("evento-1", "participante-1"));
            when(eventoService.buscarPorId("evento-1")).thenReturn(evento);
            when(inscricaoRepository.listarPorEvento("evento-1")).thenReturn(inscricoes);

            List<Inscricao> resultado = inscricaoService.listarPorEvento("evento-1");

            assertThat(resultado).isEqualTo(inscricoes);
            verify(eventoService).buscarPorId("evento-1");
            verify(inscricaoRepository).listarPorEvento("evento-1");
        }

        @Test
        @DisplayName("Deve listar inscrições por participante")
        void deveListarInscricoesPorParticipante() {
            List<Inscricao> inscricoes = List.of(criarInscricao("evento-1", "participante-1"));
            when(participanteService.buscarPorId("participante-1")).thenReturn(criarParticipante());
            when(inscricaoRepository.listarPorParticipante("participante-1")).thenReturn(inscricoes);

            List<Inscricao> resultado = inscricaoService.listarPorParticipante("participante-1");

            assertThat(resultado).isEqualTo(inscricoes);
            verify(participanteService).buscarPorId("participante-1");
            verify(inscricaoRepository).listarPorParticipante("participante-1");
        }

        @Test
        @DisplayName("Deve consultar inscrição ativa")
        void deveConsultarInscricaoAtiva() {
            Inscricao inscricao = criarInscricao("evento-1", "participante-1");
            prepararEventoEParticipanteExistentes();
            when(inscricaoRepository.buscarInscricaoAtivaPorEventoEParticipante("evento-1", "participante-1"))
                    .thenReturn(Optional.of(inscricao));

            Inscricao resultado = inscricaoService.consultar("evento-1", "participante-1");

            assertThat(resultado).isEqualTo(inscricao);
            verify(inscricaoRepository).buscarInscricaoAtivaPorEventoEParticipante("evento-1", "participante-1");
        }

        @Test
        @DisplayName("Deve lançar exceção ao consultar inscrição inexistente")
        void deveLancarExcecaoAoConsultarInscricaoInexistente() {
            prepararEventoEParticipanteExistentes();
            when(inscricaoRepository.buscarInscricaoAtivaPorEventoEParticipante("evento-1", "participante-1"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> inscricaoService.consultar("evento-1", "participante-1"))
                    .isInstanceOf(InscricaoNaoEncontradaException.class);
        }

        @Test
        @DisplayName("Deve contar inscrições confirmadas por evento")
        void deveContarInscricoesConfirmadasPorEvento() {
            when(inscricaoRepository.contarConfirmadasPorEvento("evento-1")).thenReturn(25L);

            long resultado = inscricaoService.contarConfirmadas("evento-1");

            assertThat(resultado).isEqualTo(25L);
            verify(inscricaoRepository).contarConfirmadasPorEvento("evento-1");
        }
    }

    @Nested
    @DisplayName("Testes de Regras de Negócio e Alteração")
    class Negocio {

        @Test
        @DisplayName("Deve realizar inscrição válida")
        void deveRealizarInscricaoValida() {
            prepararEventoEParticipanteExistentes();
            when(inscricaoRepository.buscarInscricaoAtivaPorEventoEParticipante("evento-1", "participante-1"))
                    .thenReturn(Optional.empty());
            when(inscricaoRepository.contarConfirmadasPorEvento("evento-1")).thenReturn(0L);
            when(inscricaoRepository.salvar(any(Inscricao.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Inscricao resultado = inscricaoService.inscrever("evento-1", "participante-1");

            assertThat(resultado.getEventoId()).isEqualTo("evento-1");
            assertThat(resultado.getParticipanteId()).isEqualTo("participante-1");
            assertThat(resultado.getStatus()).isEqualTo(StatusInscricao.CONFIRMADA);
            verify(inscricaoRepository).salvar(any(Inscricao.class));
        }

        @Test
        @DisplayName("Deve cancelar inscrição ativa")
        void deveCancelarInscricaoAtiva() {
            Inscricao inscricao = criarInscricao("evento-1", "participante-1");
            prepararEventoEParticipanteExistentes();
            when(inscricaoRepository.buscarInscricaoAtivaPorEventoEParticipante("evento-1", "participante-1"))
                    .thenReturn(Optional.of(inscricao));

            inscricaoService.cancelar("evento-1", "participante-1");

            assertThat(inscricao.getStatus()).isEqualTo(StatusInscricao.CANCELADA);
            verify(inscricaoRepository).salvar(inscricao);
        }

        @Test
        @DisplayName("Deve lançar exceção ao cancelar inscrição inexistente")
        void deveLancarExcecaoAoCancelarInscricaoInexistente() {
            prepararEventoEParticipanteExistentes();
            when(inscricaoRepository.buscarInscricaoAtivaPorEventoEParticipante("evento-1", "participante-1"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> inscricaoService.cancelar("evento-1", "participante-1"))
                    .isInstanceOf(InscricaoNaoEncontradaException.class);

            verify(inscricaoRepository, never()).salvar(any(Inscricao.class));
        }
    }

    @Nested
    @DisplayName("Testes de Validação")
    class Validacoes {

        @Test
        @DisplayName("Deve impedir inscrição em evento cancelado")
        void deveImpedirInscricaoEmEventoCancelado() {
            Evento evento = criarEventoFuturo(100);
            evento.cancelar();
            when(eventoService.buscarPorId("evento-1")).thenReturn(evento);
            when(participanteService.buscarPorId("participante-1")).thenReturn(criarParticipante());

            assertThatThrownBy(() -> inscricaoService.inscrever("evento-1", "participante-1"))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("Não é possível realizar inscrição em um evento cancelado.");

            verify(inscricaoRepository, never()).salvar(any(Inscricao.class));
        }

        @Test
        @DisplayName("Deve impedir inscrição após o início do evento")
        void deveImpedirInscricaoAposInicioDoEvento() {
            when(eventoService.buscarPorId("evento-1")).thenReturn(criarEventoPassado(100));
            when(participanteService.buscarPorId("participante-1")).thenReturn(criarParticipante());

            assertThatThrownBy(() -> inscricaoService.inscrever("evento-1", "participante-1"))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("Não é possível realizar inscrição após o início do evento.");

            verify(inscricaoRepository, never()).salvar(any(Inscricao.class));
        }

        @Test
        @DisplayName("Deve impedir inscrição duplicada")
        void deveImpedirInscricaoDuplicada() {
            prepararEventoEParticipanteExistentes();
            when(inscricaoRepository.buscarInscricaoAtivaPorEventoEParticipante("evento-1", "participante-1"))
                    .thenReturn(Optional.of(criarInscricao("evento-1", "participante-1")));

            assertThatThrownBy(() -> inscricaoService.inscrever("evento-1", "participante-1"))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("O participante já possui uma inscrição ativa neste evento.");

            verify(inscricaoRepository, never()).salvar(any(Inscricao.class));
        }

        @Test
        @DisplayName("Deve impedir inscrição quando a capacidade máxima for atingida")
        void deveImpedirInscricaoQuandoCapacidadeMaximaForAtingida() {
            when(eventoService.buscarPorId("evento-1")).thenReturn(criarEventoFuturo(1));
            when(participanteService.buscarPorId("participante-1")).thenReturn(criarParticipante());
            when(inscricaoRepository.buscarInscricaoAtivaPorEventoEParticipante("evento-1", "participante-1"))
                    .thenReturn(Optional.empty());
            when(inscricaoRepository.contarConfirmadasPorEvento("evento-1")).thenReturn(1L);

            assertThatThrownBy(() -> inscricaoService.inscrever("evento-1", "participante-1"))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("O evento atingiu sua capacidade máxima.");

            verify(inscricaoRepository, never()).salvar(any(Inscricao.class));
        }
    }

    private void prepararEventoEParticipanteExistentes() {
        when(eventoService.buscarPorId("evento-1")).thenReturn(criarEventoFuturo(100));
        when(participanteService.buscarPorId("participante-1")).thenReturn(criarParticipante());
    }

    private static Evento criarEventoFuturo(int capacidadeMaxima) {
        return new Evento("Evento de Teste", "Descrição do evento", LocalDate.now().plusDays(1),
                LocalTime.of(18, 0), LocalTime.of(20, 0), "Centro de Eventos", capacidadeMaxima);
    }

    private static Evento criarEventoPassado(int capacidadeMaxima) {
        return new Evento("Evento de Teste", "Descrição do evento", LocalDate.now().minusDays(1),
                LocalTime.of(18, 0), LocalTime.of(20, 0), "Centro de Eventos", capacidadeMaxima);
    }

    private static Participante criarParticipante() {
        return new Participante("Maria", "maria@email.com");
    }

    private static Inscricao criarInscricao(String eventoId, String participanteId) {
        return new Inscricao(eventoId, participanteId);
    }
}

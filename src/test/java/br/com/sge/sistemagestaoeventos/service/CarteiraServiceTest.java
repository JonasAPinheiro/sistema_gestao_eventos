package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.exception.ParticipanteNaoEncontradoException;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.model.carteira.Carteira;
import br.com.sge.sistemagestaoeventos.repository.CarteiraRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarteiraServiceTest {

    @Mock
    private CarteiraRepository carteiraRepository;

    @Mock
    private ParticipanteService participanteService;

    @InjectMocks
    private CarteiraService carteiraService;

    @Test
    @DisplayName("Deve criar carteira ao consultar participante sem saldo")
    void deveCriarCarteiraAoConsultarParticipanteSemSaldo() {
        when(participanteService.buscarPorId("participante-1")).thenReturn(participante());
        when(carteiraRepository.buscarPorParticipanteId("participante-1")).thenReturn(Optional.empty());
        when(carteiraRepository.salvar(any(Carteira.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Carteira resultado = carteiraService.consultar("participante-1");

        assertThat(resultado.getParticipanteId()).isEqualTo("participante-1");
        assertThat(resultado.getSaldo()).isEqualByComparingTo("0.00");
        verify(participanteService).buscarPorId("participante-1");
        verify(carteiraRepository).salvar(any(Carteira.class));
    }

    @Test
    @DisplayName("Deve retornar carteira existente ao consultar")
    void deveRetornarCarteiraExistenteAoConsultar() {
        Carteira existente = new Carteira("participante-1");
        existente.creditar(new BigDecimal("40.00"));
        when(participanteService.buscarPorId("participante-1")).thenReturn(participante());
        when(carteiraRepository.buscarPorParticipanteId("participante-1")).thenReturn(Optional.of(existente));

        Carteira resultado = carteiraService.consultar("participante-1");

        assertThat(resultado).isSameAs(existente);
        verify(carteiraRepository, never()).salvar(any(Carteira.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao consultar carteira de participante inexistente")
    void deveLancarExcecaoAoConsultarParticipanteInexistente() {
        when(participanteService.buscarPorId("99"))
                .thenThrow(new ParticipanteNaoEncontradoException("99"));

        assertThatThrownBy(() -> carteiraService.consultar("99"))
                .isInstanceOf(ParticipanteNaoEncontradoException.class);

        verify(carteiraRepository, never()).buscarPorParticipanteId(any());
    }

    @Test
    @DisplayName("Deve creditar valor na carteira existente")
    void deveCreditarValorNaCarteiraExistente() {
        Carteira existente = new Carteira("participante-1");
        when(carteiraRepository.buscarPorParticipanteId("participante-1")).thenReturn(Optional.of(existente));
        when(carteiraRepository.salvar(existente)).thenReturn(existente);

        carteiraService.creditar("participante-1", new BigDecimal("25.50"));

        assertThat(existente.getSaldo()).isEqualByComparingTo("25.50");
        verify(carteiraRepository).salvar(existente);
    }

    @Test
    @DisplayName("Deve criar carteira ao creditar participante sem saldo")
    void deveCriarCarteiraAoCreditarParticipanteSemSaldo() {
        Carteira nova = new Carteira("participante-1");
        when(carteiraRepository.buscarPorParticipanteId("participante-1")).thenReturn(Optional.empty());
        when(carteiraRepository.salvar(any(Carteira.class))).thenReturn(nova);

        carteiraService.creditar("participante-1", new BigDecimal("10.00"));

        verify(carteiraRepository, times(2)).salvar(any(Carteira.class));
        assertThat(nova.getSaldo()).isEqualByComparingTo("10.00");
    }

    @Test
    @DisplayName("Deve debitar valor da carteira")
    void deveDebitarValorDaCarteira() {
        Carteira existente = new Carteira("participante-1");
        existente.creditar(new BigDecimal("50.00"));
        when(carteiraRepository.buscarPorParticipanteId("participante-1")).thenReturn(Optional.of(existente));
        when(carteiraRepository.salvar(existente)).thenReturn(existente);

        carteiraService.debitar("participante-1", new BigDecimal("20.00"));

        assertThat(existente.getSaldo()).isEqualByComparingTo("30.00");
        verify(carteiraRepository).salvar(existente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao debitar saldo insuficiente")
    void deveLancarExcecaoAoDebitarSaldoInsuficiente() {
        Carteira existente = new Carteira("participante-1");
        existente.creditar(new BigDecimal("5.00"));

        when(carteiraRepository.buscarPorParticipanteId("participante-1"))
                .thenReturn(Optional.of(existente));

        String participanteId = "participante-1";
        BigDecimal valorDebito = new BigDecimal("5.01");

        assertThatThrownBy(() -> carteiraService.debitar(participanteId, valorDebito))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Saldo insuficiente na carteira.");

        verify(carteiraRepository, never()).salvar(any(Carteira.class));
    }

    private static Participante participante() {
        return new Participante("123", "Ana", "ana@email.com", LocalDate.of(2000, 1, 1));
    }
}

package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.model.carteira.Carteira;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CarteiraRepositoryMemoriaTest {

    private CarteiraRepositoryMemoria repository;

    @BeforeEach
    void setUp() {
        repository = new CarteiraRepositoryMemoria();
    }

    @Test
    @DisplayName("Deve salvar e retornar a mesma carteira")
    void deveSalvarERetornarCarteira() {
        Carteira carteira = new Carteira("participante-1");
        carteira.creditar(new BigDecimal("15.00"));

        Carteira resultado = repository.salvar(carteira);

        assertThat(resultado).isSameAs(carteira);
        assertThat(repository.buscarPorParticipanteId("participante-1")).containsSame(carteira);
    }

    @Test
    @DisplayName("Deve substituir carteira ao salvar novamente para o mesmo participante")
    void deveSubstituirCarteiraAoSalvarComMesmoParticipante() {
        Carteira primeira = new Carteira("participante-1");
        primeira.creditar(new BigDecimal("10.00"));
        repository.salvar(primeira);

        Carteira atualizada = new Carteira("participante-1");
        atualizada.creditar(new BigDecimal("40.00"));
        repository.salvar(atualizada);

        Optional<Carteira> resultado = repository.buscarPorParticipanteId("participante-1");

        assertThat(resultado).containsSame(atualizada);
        assertThat(resultado.get().getSaldo()).isEqualByComparingTo("40.00");
    }

    @Test
    @DisplayName("Deve retornar vazio quando o participante não possui carteira")
    void deveRetornarVazioQuandoNaoHaCarteira() {
        repository.salvar(new Carteira("participante-1"));

        assertThat(repository.buscarPorParticipanteId("participante-2")).isEmpty();
    }
}

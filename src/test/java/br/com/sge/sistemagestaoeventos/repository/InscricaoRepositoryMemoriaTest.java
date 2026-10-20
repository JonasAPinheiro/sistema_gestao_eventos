package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.enums.StatusInscricao;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InscricaoRepositoryMemoriaTest {

    private InscricaoRepositoryMemoria repository;

    @BeforeEach
    void setUp() {
        repository = new InscricaoRepositoryMemoria();
    }

    @Test
    @DisplayName("Deve salvar e retornar a mesma inscrição")
    void deveSalvarERetornarInscricao() {
        Inscricao inscricao = new Inscricao("evento-1", "participante-1");

        Inscricao resultado = repository.salvar(inscricao);

        assertThat(resultado).isSameAs(inscricao);
        assertThat(repository.listarPorEvento("evento-1")).containsExactly(inscricao);
    }

    @Test
    @DisplayName("Deve substituir inscrição ao salvar novamente com o mesmo id")
    void deveSubstituirInscricaoAoSalvarComMesmoId() {
        Inscricao inscricao = new Inscricao("evento-1", "participante-1");
        repository.salvar(inscricao);

        inscricao.cancelar("Mudança de planos");
        repository.salvar(inscricao);

        assertThat(repository.listarPorEvento("evento-1")).hasSize(1);
        assertThat(repository.listarPorEvento("evento-1").getFirst().getStatus())
                .isEqualTo(StatusInscricao.CANCELADA);
    }

    @Test
    @DisplayName("Deve buscar inscrição ativa por evento e participante")
    void deveBuscarInscricaoAtivaPorEventoEParticipante() {
        Inscricao inscricao = repository.salvar(new Inscricao("evento-1", "participante-1"));

        Optional<Inscricao> resultado =
                repository.buscarInscricaoAtivaPorEventoEParticipante("evento-1", "participante-1");

        assertThat(resultado).containsSame(inscricao);
    }

    @Test
    @DisplayName("Não deve buscar inscrição ativa de outro evento")
    void naoDeveBuscarInscricaoAtivaDeOutroEvento() {
        repository.salvar(new Inscricao("evento-1", "participante-1"));

        Optional<Inscricao> resultado =
                repository.buscarInscricaoAtivaPorEventoEParticipante("evento-2", "participante-1");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Não deve buscar inscrição ativa de outro participante")
    void naoDeveBuscarInscricaoAtivaDeOutroParticipante() {
        repository.salvar(new Inscricao("evento-1", "participante-1"));

        Optional<Inscricao> resultado =
                repository.buscarInscricaoAtivaPorEventoEParticipante("evento-1", "participante-2");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Não deve buscar inscrição cancelada como ativa")
    void naoDeveBuscarInscricaoCanceladaComoAtiva() {
        Inscricao inscricao = new Inscricao("evento-1", "participante-1");
        inscricao.cancelar("Mudança de planos");
        repository.salvar(inscricao);

        Optional<Inscricao> resultado =
                repository.buscarInscricaoAtivaPorEventoEParticipante("evento-1", "participante-1");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar vazio quando não há inscrições")
    void deveRetornarVazioQuandoNaoHaInscricoes() {
        assertThat(repository.buscarInscricaoAtivaPorEventoEParticipante("evento-1", "participante-1"))
                .isEmpty();
    }

    @Test
    @DisplayName("Deve listar apenas inscrições do evento informado")
    void deveListarApenasInscricoesDoEvento() {
        Inscricao doEvento1 = repository.salvar(new Inscricao("evento-1", "participante-1"));
        Inscricao outraDoEvento1 = repository.salvar(new Inscricao("evento-1", "participante-2"));
        repository.salvar(new Inscricao("evento-2", "participante-1"));

        List<Inscricao> resultado = repository.listarPorEvento("evento-1");

        assertThat(resultado).containsExactlyInAnyOrder(doEvento1, outraDoEvento1);
    }

    @Test
    @DisplayName("Deve listar inscrições canceladas ao listar por evento")
    void deveListarInscricoesCanceladasPorEvento() {
        Inscricao cancelada = new Inscricao("evento-1", "participante-1");
        cancelada.cancelar("Mudança de planos");
        repository.salvar(cancelada);

        assertThat(repository.listarPorEvento("evento-1")).containsExactly(cancelada);
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao listar evento sem inscrições")
    void deveRetornarListaVaziaParaEventoSemInscricoes() {
        repository.salvar(new Inscricao("evento-1", "participante-1"));

        assertThat(repository.listarPorEvento("evento-2")).isEmpty();
    }

    @Test
    @DisplayName("Deve listar apenas inscrições do participante informado")
    void deveListarApenasInscricoesDoParticipante() {
        Inscricao doParticipante1 = repository.salvar(new Inscricao("evento-1", "participante-1"));
        Inscricao outraDoParticipante1 = repository.salvar(new Inscricao("evento-2", "participante-1"));
        repository.salvar(new Inscricao("evento-1", "participante-2"));

        List<Inscricao> resultado = repository.listarPorParticipante("participante-1");

        assertThat(resultado).containsExactlyInAnyOrder(doParticipante1, outraDoParticipante1);
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao listar participante sem inscrições")
    void deveRetornarListaVaziaParaParticipanteSemInscricoes() {
        repository.salvar(new Inscricao("evento-1", "participante-1"));

        assertThat(repository.listarPorParticipante("participante-2")).isEmpty();
    }

    @Test
    @DisplayName("Deve contar apenas inscrições confirmadas do evento")
    void deveContarApenasInscricoesConfirmadasDoEvento() {
        repository.salvar(new Inscricao("evento-1", "participante-1"));
        repository.salvar(new Inscricao("evento-1", "participante-2"));

        Inscricao cancelada = new Inscricao("evento-1", "participante-3");
        cancelada.cancelar("Mudança de planos");
        repository.salvar(cancelada);

        repository.salvar(new Inscricao("evento-2", "participante-4"));

        assertThat(repository.contarConfirmadasPorEvento("evento-1")).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve retornar zero ao contar evento sem inscrições confirmadas")
    void deveRetornarZeroParaEventoSemInscricoesConfirmadas() {
        assertThat(repository.contarConfirmadasPorEvento("evento-1")).isZero();
    }
}
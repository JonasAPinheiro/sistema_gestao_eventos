package br.com.sge.sistemagestaoeventos.repository;

import br.com.sge.sistemagestaoeventos.enums.DestinoReembolso;
import br.com.sge.sistemagestaoeventos.enums.MotivoReembolso;
import br.com.sge.sistemagestaoeventos.model.reembolso.Reembolso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ReembolsoRepositoryMemoriaTest {

    private ReembolsoRepositoryMemoria repository;

    @BeforeEach
    void setUp() {
        repository = new ReembolsoRepositoryMemoria();
    }

    @Test
    @DisplayName("Deve salvar e retornar o mesmo reembolso")
    void deveSalvarERetornarReembolso() {
        Reembolso reembolso = reembolso("reembolso-1", "inscricao-1", "participante-1");

        Reembolso resultado = repository.salvar(reembolso);

        assertThat(resultado).isSameAs(reembolso);
    }

    @Test
    @DisplayName("Deve buscar reembolso pela inscrição")
    void deveBuscarReembolsoPorInscricao() {
        Reembolso reembolso = repository.salvar(reembolso("reembolso-1", "inscricao-1", "participante-1"));
        repository.salvar(reembolso("reembolso-2", "inscricao-2", "participante-2"));

        Optional<Reembolso> resultado = repository.buscarPorInscricaoId("inscricao-1");

        assertThat(resultado).containsSame(reembolso);
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar inscrição sem reembolso")
    void deveRetornarVazioQuandoInscricaoNaoTemReembolso() {
        repository.salvar(reembolso("reembolso-1", "inscricao-1", "participante-1"));

        assertThat(repository.buscarPorInscricaoId("inscricao-2")).isEmpty();
    }

    @Test
    @DisplayName("Deve listar apenas reembolsos do participante informado")
    void deveListarApenasReembolsosDoParticipante() {
        Reembolso primeiro = repository.salvar(reembolso("reembolso-1", "inscricao-1", "participante-1"));
        Reembolso segundo = repository.salvar(reembolso("reembolso-2", "inscricao-2", "participante-1"));
        repository.salvar(reembolso("reembolso-3", "inscricao-3", "participante-2"));

        List<Reembolso> resultado = repository.listarPorParticipante("participante-1");

        assertThat(resultado).containsExactlyInAnyOrder(primeiro, segundo);
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao listar participante sem reembolsos")
    void deveRetornarListaVaziaParaParticipanteSemReembolsos() {
        repository.salvar(reembolso("reembolso-1", "inscricao-1", "participante-1"));

        assertThat(repository.listarPorParticipante("participante-2")).isEmpty();
    }

    private static Reembolso reembolso(String id, String inscricaoId, String participanteId) {
        return new Reembolso(id, inscricaoId, participanteId, new BigDecimal("80.00"),
                DestinoReembolso.CARTEIRA, MotivoReembolso.SOLICITADO_PARTICIPANTE, null);
    }
}

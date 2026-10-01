package br.com.sge.sistemagestaoeventos.model.modalidade.factory;

import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.factory.modalidade.CriadorModalidadeAberta;
import br.com.sge.sistemagestaoeventos.factory.modalidade.CriadorModalidadeComRestricaoDeIdade;
import br.com.sge.sistemagestaoeventos.factory.modalidade.CriadorModalidadeExclusivaParaAlunos;
import br.com.sge.sistemagestaoeventos.factory.modalidade.ModalidadeEventoFactory;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeAberta;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeComRestricaoDeIdade;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeExclusivaParaAlunos;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeEvento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModalidadeEventoFactoryTest {

    private final ModalidadeEventoFactory factory = new ModalidadeEventoFactory(List.of(
            new CriadorModalidadeAberta(),
            new CriadorModalidadeExclusivaParaAlunos(),
            new CriadorModalidadeComRestricaoDeIdade()
    ));

    @Test
    @DisplayName("Deve criar modalidade aberta")
    void deveCriarModalidadeAberta() {
        ModalidadeEvento modalidade = factory.criar(TipoModalidade.ABERTO, null);

        assertThat(modalidade).isInstanceOf(ModalidadeAberta.class);
        assertThat(modalidade.getTipo()).isEqualTo(TipoModalidade.ABERTO);
    }

    @Test
    @DisplayName("Deve criar modalidade exclusiva para alunos")
    void deveCriarModalidadeExclusivaParaAlunos() {
        ModalidadeEvento modalidade = factory.criar(TipoModalidade.EXCLUSIVO_ALUNOS, null);

        assertThat(modalidade).isInstanceOf(ModalidadeExclusivaParaAlunos.class);
        assertThat(modalidade.getTipo()).isEqualTo(TipoModalidade.EXCLUSIVO_ALUNOS);
    }

    @Test
    @DisplayName("Deve criar modalidade com restrição de idade")
    void deveCriarModalidadeComRestricaoDeIdade() {
        ModalidadeEvento modalidade = factory.criar(TipoModalidade.RESTRICAO_IDADE, 18);

        assertThat(modalidade).isInstanceOf(ModalidadeComRestricaoDeIdade.class);
        assertThat(modalidade.getTipo()).isEqualTo(TipoModalidade.RESTRICAO_IDADE);
        assertThat(((ModalidadeComRestricaoDeIdade) modalidade).getIdadeMinima())
                .isEqualTo(18);
    }

    @Test
    @DisplayName("Deve usar modalidade aberta quando o tipo não for informado")
    void deveUsarModalidadeAbertaComoPadrao() {
        ModalidadeEvento modalidade = factory.criar(null, null);

        assertThat(modalidade).isInstanceOf(ModalidadeAberta.class);
        assertThat(modalidade.getTipo()).isEqualTo(TipoModalidade.ABERTO);
    }

    @Test
    @DisplayName("Deve rejeitar idade mínima inválida na modalidade com restrição")
    void deveRejeitarIdadeMinimaInvalida() {
        assertThatThrownBy(() -> factory.criar(TipoModalidade.RESTRICAO_IDADE, 0))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("A idade mínima do evento deve ser um número inteiro maior que zero.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando não existir criador para o tipo")
    void deveLancarExcecaoQuandoNaoExistirCriador() {
        ModalidadeEventoFactory factorySemCriador = new ModalidadeEventoFactory(
                List.of(new CriadorModalidadeAberta())
        );

        assertThatThrownBy(() -> factorySemCriador.criar(TipoModalidade.EXCLUSIVO_ALUNOS, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Nenhum criador configurado para a modalidade EXCLUSIVO_ALUNOS.");
    }
}

package br.com.sge.sistemagestaoeventos.model.comprovante;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CarregadorRecursoDiscoTest {

    @TempDir
    Path diretorioTemporario;

    private final CarregadorRecursoDisco carregador = new CarregadorRecursoDisco();

    @Test
    @DisplayName("Deve carregar recurso válido existente no disco")
    void deveCarregarRecursoValido() throws Exception {

        Path arquivo = diretorioTemporario.resolve("meu-comprovante.txt");
        Files.writeString(arquivo, "dados do comprovante");

        Resource recurso = carregador.carregar(arquivo);

        assertThat(recurso.exists()).isTrue();
        assertThat(recurso.isReadable()).isTrue();
        assertThat(recurso.getFilename()).isEqualTo("meu-comprovante.txt");
    }

    @Test
    @DisplayName("Deve lançar exceção quando o arquivo não existir no disco")
    void deveLancarExcecaoQuandoArquivoNaoExistir() {

        Path arquivoInexistente = diretorioTemporario.resolve("arquivo-fantasma.txt");

        assertThatThrownBy(() -> carregador.carregar(arquivoInexistente))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("O arquivo não está acessível para leitura no disco.");
    }

    @Test
    @DisplayName("Deve lançar exceção de infraestrutura ao ocorrer erro de formato de URI (MalformedURLException)")
    void deveLancarExcecaoAoOcorrerMalformedURLException() {
        Path pathInvalidoMock = mock(Path.class);
        when(pathInvalidoMock.toUri()).thenReturn(URI.create("esquema-invalido://meu-arquivo.txt"));

        assertThatThrownBy(() -> carregador.carregar(pathInvalidoMock))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Erro interno ao processar o caminho do arquivo.");
    }
}
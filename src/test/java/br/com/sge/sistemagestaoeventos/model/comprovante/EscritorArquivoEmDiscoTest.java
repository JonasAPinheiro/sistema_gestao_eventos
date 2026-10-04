package br.com.sge.sistemagestaoeventos.model.comprovante;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EscritorArquivoEmDiscoTest {

    @TempDir
    Path diretorioTemporario;

    @Test
    @DisplayName("Deve escrever conteúdo no arquivo")
    void deveEscreverConteudoNoArquivo() throws Exception {

        EscritorArquivoEmDisco escritor = new EscritorArquivoEmDisco();
        Path destino = diretorioTemporario.resolve("comprovante.txt");

        Path resultado = escritor.escrever(destino, "conteúdo do comprovante");

        assertThat(resultado).isEqualTo(destino);
        assertThat(Files.exists(destino)).isTrue();
        assertThat(Files.readString(destino)).isEqualTo("conteúdo do comprovante");
    }

    @Test
    @DisplayName("Deve lançar UncheckedIOException ao falhar na gravação (ex: caminho inválido)")
    void deveLancarExcecaoAoFalharGravacao() throws Exception {

        EscritorArquivoEmDisco escritor = new EscritorArquivoEmDisco();
        Path arquivoExistente = Files.createFile(diretorioTemporario.resolve("arquivo.txt"));
        Path destinoInvalido = arquivoExistente.resolve("comprovante.txt");

        assertThatThrownBy(() -> escritor.escrever(destinoInvalido, "conteúdo"))
                .isInstanceOf(UncheckedIOException.class)
                .hasMessageContaining("Falha ao gravar o arquivo");
    }
}
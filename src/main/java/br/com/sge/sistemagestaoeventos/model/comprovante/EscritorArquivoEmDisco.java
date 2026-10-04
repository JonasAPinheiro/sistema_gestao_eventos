package br.com.sge.sistemagestaoeventos.model.comprovante;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class EscritorArquivoEmDisco implements EscritorArquivo {

    @Override
    public Path escrever(Path destino, String conteudo) {
        try {
            Files.createDirectories(destino.toAbsolutePath().getParent());
            return Files.writeString(destino, conteudo, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao gravar o arquivo " + destino + ".", e);
        }
    }
}
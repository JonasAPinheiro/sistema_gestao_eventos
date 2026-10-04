package br.com.sge.sistemagestaoeventos.model.comprovante;

import java.nio.file.Path;

public interface EscritorArquivo {
    Path escrever(Path destino, String conteudo);
}

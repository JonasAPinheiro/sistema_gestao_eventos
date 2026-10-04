package br.com.sge.sistemagestaoeventos.model.comprovante;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;

import java.nio.file.Path;

public interface ExportadorEmArquivo {
    TipoComprovante getTipo();
    Path exportarParaArquivo(Comprovante comprovante);
}
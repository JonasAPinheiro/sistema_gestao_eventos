package br.com.sge.sistemagestaoeventos.model.comprovante;

import org.springframework.core.io.Resource;
import java.nio.file.Path;

public interface CarregadorRecurso {
    Resource carregar(Path caminho);
}
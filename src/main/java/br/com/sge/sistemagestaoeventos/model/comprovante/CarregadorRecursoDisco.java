package br.com.sge.sistemagestaoeventos.model.comprovante;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.nio.file.Path;

@Component
public class CarregadorRecursoDisco implements CarregadorRecurso {

    @Override
    public Resource carregar(Path caminho) {
        try {
            Resource resource = new UrlResource(caminho.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalStateException("O arquivo não está acessível para leitura no disco.");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Erro interno ao processar o caminho do arquivo.", e);
        }
    }
}
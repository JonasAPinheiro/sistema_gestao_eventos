package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.comprovante.Comprovante;
import br.com.sge.sistemagestaoeventos.model.comprovante.ExportadorEmArquivo;
import br.com.sge.sistemagestaoeventos.model.comprovante.CarregadorRecurso;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
public class ExportacaoComprovanteService {

    private final InscricaoService inscricaoService;
    private final List<ExportadorEmArquivo> exportadores;
    private final CarregadorRecurso carregadorRecurso;

    public ExportacaoComprovanteService(
            InscricaoService inscricaoService,
            List<ExportadorEmArquivo> exportadores,
            CarregadorRecurso carregadorRecurso) {
        this.inscricaoService = inscricaoService;
        this.exportadores = exportadores;
        this.carregadorRecurso = carregadorRecurso;
    }

    public Resource exportar(String eventoId, String participanteId) {
        Inscricao inscricao = inscricaoService.consultar(eventoId, participanteId);
        Comprovante comprovante = inscricao.getComprovante();

        if (comprovante == null) {
            throw new RegraNegocioException("Esta inscrição ainda não possui um comprovante gerado.");
        }

        Path caminho = exportadores.stream()
                .filter(exportador -> exportador.getTipo() == comprovante.getTipo())
                .findFirst()
                .orElseThrow(() -> new RegraNegocioException("O formato " + comprovante.getTipo() + " não suporta download de arquivo."))
                .exportarParaArquivo(comprovante);

        return carregadorRecurso.carregar(caminho);
    }
}
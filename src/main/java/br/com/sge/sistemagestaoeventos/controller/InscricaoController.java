package br.com.sge.sistemagestaoeventos.controller;

import br.com.sge.sistemagestaoeventos.dto.InscricaoRequestDTO;
import br.com.sge.sistemagestaoeventos.dto.InscricaoResponseDTO;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.comprovante.Comprovante;
import br.com.sge.sistemagestaoeventos.service.ComprovanteService;
import br.com.sge.sistemagestaoeventos.service.ExportacaoComprovanteService;
import br.com.sge.sistemagestaoeventos.service.InscricaoService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@RestController
public class InscricaoController {

    private final InscricaoService inscricaoService;
    private final ComprovanteService comprovanteService;
    private final ExportacaoComprovanteService exportacaoComprovanteService;

    public InscricaoController(
            InscricaoService inscricaoService,
            ComprovanteService comprovanteService,
            ExportacaoComprovanteService exportacaoComprovanteService) {
        this.inscricaoService = inscricaoService;
        this.comprovanteService = comprovanteService;
        this.exportacaoComprovanteService = exportacaoComprovanteService;
    }

    @PostMapping("/eventos/{eventoId}/inscricoes")
    @ResponseStatus(HttpStatus.CREATED)
    public InscricaoResponseDTO inscrever(@PathVariable String eventoId, @RequestBody InscricaoRequestDTO dto) {
        Inscricao inscricao = inscricaoService.inscrever(eventoId, dto.participanteId());
        Comprovante comprovante = comprovanteService.emitir(inscricao);
        inscricao.setComprovante(comprovante);
        return InscricaoResponseDTO.from(inscricao);
    }

    @DeleteMapping("/eventos/{eventoId}/inscricoes/{participanteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelar(@PathVariable String eventoId, @PathVariable String participanteId, @RequestParam(required = false) String motivo) {
        inscricaoService.cancelar(eventoId, participanteId, motivo);
    }

    @GetMapping("/eventos/{eventoId}/inscricoes")
    public List<InscricaoResponseDTO> listarPorEvento(@PathVariable String eventoId) {
        return inscricaoService.listarPorEvento(eventoId).stream()
                .map(InscricaoResponseDTO::from)
                .toList();
    }

    @GetMapping("/participantes/{participanteId}/inscricoes")
    public List<InscricaoResponseDTO> listarPorParticipante(@PathVariable String participanteId) {
        return inscricaoService.listarPorParticipante(participanteId).stream()
                .map(InscricaoResponseDTO::from)
                .toList();
    }

    @GetMapping("/eventos/{eventoId}/inscricoes/{participanteId}")
    public InscricaoResponseDTO consultar(@PathVariable String eventoId, @PathVariable String participanteId) {
        return InscricaoResponseDTO.from(inscricaoService.consultar(eventoId, participanteId));
    }

    @GetMapping(value = "/eventos/{eventoId}/inscricoes/{participanteId}/comprovante/download", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Resource> downloadComprovante(@PathVariable String eventoId, @PathVariable String participanteId) {

        Resource resource = exportacaoComprovanteService.exportar(eventoId, participanteId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
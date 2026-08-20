package br.com.sge.sistemagestaoeventos.controller;

import br.com.sge.sistemagestaoeventos.dto.InscricaoRequestDTO;
import br.com.sge.sistemagestaoeventos.dto.InscricaoResponseDTO;
import br.com.sge.sistemagestaoeventos.service.EventoService;
import br.com.sge.sistemagestaoeventos.service.InscricaoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InscricaoController {

    private final InscricaoService inscricaoService;
    private final EventoService eventoService;

    public InscricaoController(InscricaoService inscricaoService, EventoService eventoService) {
        this.inscricaoService = inscricaoService;
        this.eventoService = eventoService;
    }

    @PostMapping("/eventos/{eventoId}/inscricoes")
    @ResponseStatus(HttpStatus.CREATED)
    public InscricaoResponseDTO inscrever(@PathVariable String eventoId, @RequestBody InscricaoRequestDTO dto) {
        return InscricaoResponseDTO.from(inscricaoService.inscrever(eventoId, dto.participanteId()));
    }

    @DeleteMapping("/eventos/{eventoId}/inscricoes/{participanteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelar(@PathVariable String eventoId, @PathVariable String participanteId) {
        inscricaoService.cancelar(eventoId, participanteId);
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
}
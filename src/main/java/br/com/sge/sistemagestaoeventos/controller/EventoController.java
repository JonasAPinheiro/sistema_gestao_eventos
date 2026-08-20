package br.com.sge.sistemagestaoeventos.controller;

import br.com.sge.sistemagestaoeventos.dto.EventoRequestDTO;
import br.com.sge.sistemagestaoeventos.dto.EventoResponseDTO;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.service.EventoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {
    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }
    @GetMapping("/{id}")
    public EventoResponseDTO buscarPorId(@PathVariable String id) {
        return EventoResponseDTO.from(eventoService.buscarPorId(id));
    }

    @GetMapping
    public List<EventoResponseDTO> listarTodos() {
        return eventoService.listarTodos().stream()
                .map(EventoResponseDTO::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventoResponseDTO cadastrar(@RequestBody EventoRequestDTO dto) {
        Evento evento = new Evento(
                dto.titulo(),
                dto.descricao(),
                dto.data(),
                dto.horaInicio(),
                dto.horaFim(),
                dto.local(),
                dto.capacidadeMaxima()
        );
        return EventoResponseDTO.from(eventoService.cadastrar(evento));
    }

    @PutMapping("/{id}")
    public EventoResponseDTO atualizar(@PathVariable String id, @RequestBody EventoRequestDTO dto) {
        Evento dadosAtualizados = new Evento(
                dto.titulo(),
                dto.descricao(),
                dto.data(),
                dto.horaInicio(),
                dto.horaFim(),
                dto.local(),
                dto.capacidadeMaxima()
        );
        return EventoResponseDTO.from(eventoService.atualizar(id, dadosAtualizados));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelar(@PathVariable String id) {
        eventoService.cancelar(id);
    }
}
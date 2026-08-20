package br.com.sge.sistemagestaoeventos.controller;

import br.com.sge.sistemagestaoeventos.dto.EventoRequestDTO;
import br.com.sge.sistemagestaoeventos.dto.EventoResponseDTO;
import br.com.sge.sistemagestaoeventos.dto.VagasResponseDTO;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.service.EventoService;
import br.com.sge.sistemagestaoeventos.service.InscricaoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {
    private final EventoService eventoService;
    private final InscricaoService inscricaoService;

    public EventoController(EventoService eventoService, InscricaoService inscricaoService) {
        this.eventoService = eventoService;
        this.inscricaoService = inscricaoService;
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

    @PatchMapping("/{id}/cancelamento")
    public EventoResponseDTO cancelar(@PathVariable String id) {
        return EventoResponseDTO.from(eventoService.cancelar(id));
    }

    @GetMapping("/{id}/vagas")
    public VagasResponseDTO consultarVagas(@PathVariable String id) {
        Evento evento = eventoService.buscarPorId(id);
        long confirmadas = inscricaoService.contarConfirmadas(id);
        return new VagasResponseDTO(
                evento.getCapacidadeMaxima(),
                confirmadas,
                evento.getCapacidadeMaxima() - confirmadas
        );
    }
}
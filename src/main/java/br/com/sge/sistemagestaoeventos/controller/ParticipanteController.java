package br.com.sge.sistemagestaoeventos.controller;

import br.com.sge.sistemagestaoeventos.dto.ParticipanteRequestDTO;
import br.com.sge.sistemagestaoeventos.dto.ParticipanteResponseDTO;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.service.ParticipanteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participantes")
public class ParticipanteController {

    private final ParticipanteService participanteService;

    public ParticipanteController(ParticipanteService participanteService) {
        this.participanteService = participanteService;
    }

    @GetMapping("/{id}")
    public ParticipanteResponseDTO buscarPorId(@PathVariable String id) {
        return ParticipanteResponseDTO.from(participanteService.buscarPorId(id));
    }

    @GetMapping
    public List<ParticipanteResponseDTO> listarTodos() {
        return participanteService.listarTodos().stream()
                .map(ParticipanteResponseDTO::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipanteResponseDTO cadastrar(@RequestBody ParticipanteRequestDTO dto) {
        Participante participante = new Participante(
                dto.nome(),
                dto.email()
        );
        return ParticipanteResponseDTO.from(participanteService.cadastrar(participante));
    }

    @PutMapping("/{id}")
    public ParticipanteResponseDTO atualizar(@PathVariable String id, @RequestBody ParticipanteRequestDTO dto) {
        Participante dadosAtualizados = new Participante(
                dto.nome(),
                dto.email()
        );
        return ParticipanteResponseDTO.from(participanteService.atualizar(id, dadosAtualizados));
    }
}

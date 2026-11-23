package br.com.sge.sistemagestaoeventos.controller;

import br.com.sge.sistemagestaoeventos.dto.CarteiraResponseDTO;
import br.com.sge.sistemagestaoeventos.dto.ReembolsoRequestDTO;
import br.com.sge.sistemagestaoeventos.dto.ReembolsoResponseDTO;
import br.com.sge.sistemagestaoeventos.service.CarteiraService;
import br.com.sge.sistemagestaoeventos.service.ReembolsoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReembolsoController {

    private final ReembolsoService reembolsoService;
    private final CarteiraService carteiraService;

    public ReembolsoController(ReembolsoService reembolsoService, CarteiraService carteiraService) {
        this.reembolsoService = reembolsoService;
        this.carteiraService = carteiraService;
    }

    @PostMapping("/eventos/{eventoId}/inscricoes/{participanteId}/reembolso")
    @ResponseStatus(HttpStatus.CREATED)
    public ReembolsoResponseDTO solicitar(@PathVariable String eventoId,
                                          @PathVariable String participanteId,
                                          @RequestBody ReembolsoRequestDTO dto) {
        return ReembolsoResponseDTO.from(reembolsoService.solicitar(eventoId, participanteId, dto.destino()));
    }

    @GetMapping("/participantes/{participanteId}/carteira")
    public CarteiraResponseDTO consultarCarteira(@PathVariable String participanteId) {
        return CarteiraResponseDTO.from(carteiraService.consultar(participanteId));
    }
}
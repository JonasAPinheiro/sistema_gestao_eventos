package br.com.sge.sistemagestaoeventos.controller;

import br.com.sge.sistemagestaoeventos.dto.NotificacaoResponseDTO;
import br.com.sge.sistemagestaoeventos.service.NotificacaoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {
    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping
    public List<NotificacaoResponseDTO> listarTodas() {
        return notificacaoService.listarTodas().stream().map(NotificacaoResponseDTO::from).toList();
    }
}

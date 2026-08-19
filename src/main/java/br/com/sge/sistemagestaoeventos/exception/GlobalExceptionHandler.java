package br.com.sge.sistemagestaoeventos.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EventoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> tratarNaoEncontrado(EventoNaoEncontradoException ex, HttpServletRequest request) {
        return construirResposta(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ParticipanteNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> tratarParticipanteNaoEncontrado(ParticipanteNaoEncontradoException ex, HttpServletRequest request) {
        return construirResposta(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, Object>> tratarRegraNegocio(RegraNegocioException ex, HttpServletRequest request) {
        return construirResposta(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    private ResponseEntity<Map<String, Object>> construirResposta(HttpStatus status, String mensagem, String path) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", LocalDateTime.now());
        corpo.put("status", status.value());
        corpo.put("error", status.name());
        corpo.put("message", mensagem);
        corpo.put("path", path);
        return ResponseEntity.status(status).body(corpo);
    }
}

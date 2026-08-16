package br.com.sge.sistemagestaoeventos.exception;

public class EventoNaoEncontradoException extends RuntimeException {
    public EventoNaoEncontradoException(String id) {
        super("Evento não encontrado com o id: " + id);
    }
}
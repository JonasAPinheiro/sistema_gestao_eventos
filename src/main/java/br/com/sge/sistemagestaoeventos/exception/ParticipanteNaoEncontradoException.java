package br.com.sge.sistemagestaoeventos.exception;

public class ParticipanteNaoEncontradoException extends RuntimeException {
    public ParticipanteNaoEncontradoException(String id) {
        super("Participante não encontrado com o id: " + id);
    }
}

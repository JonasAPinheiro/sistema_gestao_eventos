package br.com.sge.sistemagestaoeventos.exception;

public class StatusInvalidoException extends RuntimeException{
    public StatusInvalidoException(String mensagem) {
        super(mensagem);
    }
}

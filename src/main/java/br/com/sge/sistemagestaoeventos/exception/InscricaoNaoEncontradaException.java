package br.com.sge.sistemagestaoeventos.exception;

public class InscricaoNaoEncontradaException extends RuntimeException {
    public InscricaoNaoEncontradaException(String eventoId, String participanteId) {
        super("Inscrição não encontrada para o participante " + participanteId + " no evento " + eventoId);
    }
}
package br.com.sge.sistemagestaoeventos.model.comprovante;

import java.time.LocalDateTime;

public interface GeradorHashInscricao {
    String gerar(String eventoId, String participanteId, LocalDateTime criadoEm);
}

package br.com.sge.sistemagestaoeventos.model.comprovante;

public record PayloadQrCode(String hash, String inscricaoId, String eventoId, String participanteId) {
    private static final String PREFIXO = "SGE-QR";

    @Override
    public String toString() {
        return "%s|inscricao=%s|evento=%s|participante=%s|hash=%s"
                .formatted(PREFIXO, inscricaoId, eventoId, participanteId, hash);
    }
}

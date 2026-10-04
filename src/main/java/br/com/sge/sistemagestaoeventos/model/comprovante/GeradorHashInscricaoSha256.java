package br.com.sge.sistemagestaoeventos.model.comprovante;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Component
public class GeradorHashInscricaoSha256 implements GeradorHashInscricao {
    private static final String ALGORITMO = "SHA-256";
    private static final String SEPARADOR = "|";
    @Override
    public String gerar(String eventoId, String participanteId, LocalDateTime criadoEm) {
        String entrada = String.join(SEPARADOR, eventoId, participanteId, criadoEm.toString());
        return HexFormat.of().formatHex(calcularResumo(entrada));
    }

    private byte[] calcularResumo(String entrada) {
        try {
            return MessageDigest.getInstance(ALGORITMO).digest(entrada.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo " + ALGORITMO + " indisponível na JVM.", e);
        }
    }
}

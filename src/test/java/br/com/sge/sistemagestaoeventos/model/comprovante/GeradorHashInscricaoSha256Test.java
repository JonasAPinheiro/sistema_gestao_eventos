package br.com.sge.sistemagestaoeventos.model.comprovante;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeradorHashInscricaoSha256Test {

    private final GeradorHashInscricaoSha256 gerador = new GeradorHashInscricaoSha256();

    @Test
    @DisplayName("Deve gerar hash SHA-256 determinístico para os mesmos dados")
    void deveGerarHashDeterministico() {

        LocalDateTime criadoEm = LocalDateTime.of(2026, 10, 1, 10, 30);

        String hash1 = gerador.gerar("evento-1", "participante-1", criadoEm);
        String hash2 = gerador.gerar("evento-1", "participante-1", criadoEm);

        assertThat(hash1)
                .isEqualTo(hash2)
                .hasSize(64);
    }

    @Test
    @DisplayName("Deve gerar hashes diferentes para dados diferentes")
    void deveGerarHashesDiferentes() {

        LocalDateTime criadoEm = LocalDateTime.of(2026, 10, 1, 10, 30);

        String hash1 = gerador.gerar("evento-1", "participante-1", criadoEm);
        String hash2 = gerador.gerar("evento-2", "participante-1", criadoEm);

        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o algoritmo SHA-256 não estiver disponível na JVM")
    void deveLancarExcecaoQuandoAlgoritmoIndisponivel() {

        LocalDateTime criadoEm = LocalDateTime.now();

        try (MockedStatic<MessageDigest> mockedDigest = Mockito.mockStatic(MessageDigest.class)) {

            mockedDigest.when(() -> MessageDigest.getInstance("SHA-256"))
                    .thenThrow(new NoSuchAlgorithmException("Simulação de algoritmo não encontrado"));

            assertThatThrownBy(() -> gerador.gerar("evento-1", "participante-1", criadoEm))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Algoritmo SHA-256 indisponível na JVM");
        }
    }
}
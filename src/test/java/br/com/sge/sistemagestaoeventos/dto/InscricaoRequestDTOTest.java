package br.com.sge.sistemagestaoeventos.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class InscricaoRequestDTOTest {

    @Test
    @DisplayName("Deve criar inscrição informando somente o participante")
    void deveCriarInscricaoSemFormaPagamento() {
        InscricaoRequestDTO dto = new InscricaoRequestDTO("participante-1");

        assertThat(dto.participanteId()).isEqualTo("participante-1");
        assertThat(dto.formaPagamento()).isNull();
    }

}

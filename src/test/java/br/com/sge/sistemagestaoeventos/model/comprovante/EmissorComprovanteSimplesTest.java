package br.com.sge.sistemagestaoeventos.model.comprovante;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeAberta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

class EmissorComprovanteSimplesTest {

    private static final DateTimeFormatter FORMATO_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final EmissorComprovanteSimples emissor =
            new EmissorComprovanteSimples();

    @Test
    @DisplayName("Deve emitir comprovante simples com resumo básico da inscrição")
    void deveEmitirComprovanteSimples() {

        Inscricao inscricao = new Inscricao("evento-1", "participante-1");

        Evento evento = new Evento(
                "Workshop de Java",
                "Descrição do evento",
                LocalDate.of(2026, 11, 10),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0),
                "Auditório Central",
                50,
                new ModalidadeAberta()
        );

        Participante participante = new Participante(
                "MAT-001",
                "Maria Silva",
                "maria@email.com",
                LocalDate.of(1995, 5, 20)
        );

        Comprovante resultado =
                emissor.emitir(inscricao, evento, participante);

        assertThat(resultado)
                .isNotInstanceOf(ComprovanteDigital.class);

        assertThat(resultado.getTipo())
                .isEqualTo(TipoComprovante.SIMPLES);

        assertThat(resultado.getInscricaoId())
                .isEqualTo(inscricao.getId());

        assertThat(resultado.getConteudo())
                .contains("COMPROVANTE DE INSCRIÇÃO")
                .contains("Inscrição: " + inscricao.getId())
                .contains("Evento: Workshop de Java")
                .contains("Participante: Maria Silva")
                .contains(
                        "Data da inscrição: "
                                + inscricao.getCriadoEm().format(FORMATO_DATA_HORA)
                );

        assertThat(resultado.obterPayloadQrCode())
                .isEmpty();
    }

    @Test
    @DisplayName("Deve retornar o tipo SIMPLES")
    void deveRetornarTipoSimples() {

        assertThat(emissor.getTipo())
                .isEqualTo(TipoComprovante.SIMPLES);
    }

    @Test
    @DisplayName("Emissor simples não deve implementar capacidades de QR Code ou exportação em arquivo")
    void emissorSimplesNaoDeveImplementarCapacidadesAvancadas() {

        assertThat(emissor)
                .isInstanceOf(EmissorComprovante.class)
                .isNotInstanceOf(GeradorQrCode.class)
                .isNotInstanceOf(ExportadorEmArquivo.class);
    }
}

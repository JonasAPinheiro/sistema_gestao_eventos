package br.com.sge.sistemagestaoeventos.model.comprovante;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.enums.TipoModalidade;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeEvento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmissorComprovanteDigitalTest {

    @Mock
    private GeradorHashInscricao geradorHash;

    @Mock
    private EscritorArquivo escritorArquivo;

    @Mock
    private Inscricao inscricao;

    @Mock
    private ModalidadeEvento modalidade;

    @Test
    @DisplayName("Deve emitir comprovante digital")
    void deveEmitirComprovanteDigital() {

        LocalDateTime criadoEm =
                LocalDateTime.of(2026, 10, 1, 10, 30);

        when(inscricao.getId())
                .thenReturn("inscricao-1");

        when(inscricao.getEventoId())
                .thenReturn("evento-1");

        when(inscricao.getParticipanteId())
                .thenReturn("participante-1");

        when(inscricao.getCriadoEm())
                .thenReturn(criadoEm);

        when(geradorHash.gerar(
                "evento-1",
                "participante-1",
                criadoEm
        )).thenReturn("hash-123");

        when(modalidade.getTipo())
                .thenReturn(TipoModalidade.ABERTO);

        Evento evento = new Evento(
                "Evento de Teste",
                "Descrição do evento",
                LocalDate.of(2026, 11, 10),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0),
                "Local do evento",
                100,
                modalidade
        );

        Participante participante = new Participante(
                "MAT-001",
                "Ronaldo Junior",
                "ronaldo@email.com",
                LocalDate.of(2000, 1, 1)
        );

        EmissorComprovanteDigital emissor =
                new EmissorComprovanteDigital(
                        geradorHash,
                        escritorArquivo,
                        "comprovantes"
                );

        Comprovante resultado =
                emissor.emitir(
                        inscricao,
                        evento,
                        participante
                );

        assertThat(resultado)
                .isInstanceOf(ComprovanteDigital.class);

        assertThat(resultado.getTipo())
                .isEqualTo(TipoComprovante.DIGITAL_COMPLETO);

        assertThat(resultado.getInscricaoId())
                .isEqualTo("inscricao-1");

        assertThat(resultado.getConteudo())
                .contains("COMPROVANTE DIGITAL DE INSCRIÇÃO")
                .contains("Inscrição: inscricao-1")
                .contains("Participante: Ronaldo Junior")
                .contains("Evento: Evento de Teste")
                .contains("Modalidade: ABERTO")
                .contains("Descrição: Descrição do evento")
                .contains("Local: Local do evento")
                .contains("Código de validação (QR Code): hash-123")
                .contains("Payload QR Code:");

        assertThat(resultado.obterPayloadQrCode())
                .isPresent()
                .get()
                .asString()
                .contains("SGE-QR")
                .contains("hash=hash-123");
    }

    @Test
    @DisplayName("Deve gerar payload QR Code utilizando os dados da inscrição")
    void deveGerarPayloadQrCode() {

        LocalDateTime criadoEm =
                LocalDateTime.of(2026, 10, 1, 10, 30);

        when(inscricao.getId())
                .thenReturn("inscricao-1");

        when(inscricao.getEventoId())
                .thenReturn("evento-1");

        when(inscricao.getParticipanteId())
                .thenReturn("participante-1");

        when(inscricao.getCriadoEm())
                .thenReturn(criadoEm);

        when(geradorHash.gerar(
                "evento-1",
                "participante-1",
                criadoEm
        )).thenReturn("hash-123");

        EmissorComprovanteDigital emissor =
                new EmissorComprovanteDigital(
                        geradorHash,
                        escritorArquivo,
                        "comprovantes"
                );

        PayloadQrCode payload =
                emissor.gerarPayloadQrCode(inscricao);

        assertThat(payload.hash())
                .isEqualTo("hash-123");

        assertThat(payload.inscricaoId())
                .isEqualTo("inscricao-1");

        assertThat(payload.eventoId())
                .isEqualTo("evento-1");

        assertThat(payload.participanteId())
                .isEqualTo("participante-1");
    }

    @Test
    @DisplayName("Deve retornar o tipo DIGITAL_COMPLETO")
    void deveRetornarTipoDigitalCompleto() {

        EmissorComprovanteDigital emissor =
                new EmissorComprovanteDigital(
                        geradorHash,
                        escritorArquivo,
                        "comprovantes"
                );

        assertThat(emissor.getTipo())
                .isEqualTo(TipoComprovante.DIGITAL_COMPLETO);
    }

    @Test
    @DisplayName("Emissor digital deve implementar interfaces segregadas de emissão, QR Code e exportação")
    void emissorDigitalDeveImplementarInterfacesSegregadas() {

        EmissorComprovanteDigital emissor =
                new EmissorComprovanteDigital(
                        geradorHash,
                        escritorArquivo,
                        "comprovantes"
                );

        assertThat(emissor)
                .isInstanceOf(EmissorComprovante.class)
                .isInstanceOf(GeradorQrCode.class)
                .isInstanceOf(ExportadorEmArquivo.class);
    }

    @Test
    @DisplayName("Deve exportar comprovante digital para arquivo em disco")
    void deveExportarComprovanteParaArquivo() {

        Comprovante comprovante = new Comprovante(
                "inscricao-1",
                TipoComprovante.DIGITAL_COMPLETO,
                "conteudo do comprovante"
        );

        Path destinoEsperado =
                Path.of("comprovantes/comprovante-inscricao-1.txt");

        when(escritorArquivo.escrever(
                eq(destinoEsperado),
                eq("conteudo do comprovante")
        )).thenReturn(destinoEsperado);

        EmissorComprovanteDigital emissor =
                new EmissorComprovanteDigital(
                        geradorHash,
                        escritorArquivo,
                        "comprovantes"
                );

        Path resultado =
                emissor.exportarParaArquivo(comprovante);

        assertThat(resultado)
                .isEqualTo(destinoEsperado);

        verify(escritorArquivo)
                .escrever(
                        eq(destinoEsperado),
                        eq("conteudo do comprovante")
                );
    }
}
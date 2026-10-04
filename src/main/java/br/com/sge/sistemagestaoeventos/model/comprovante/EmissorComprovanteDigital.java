package br.com.sge.sistemagestaoeventos.model.comprovante;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

@Component
public class EmissorComprovanteDigital implements EmissorComprovante, GeradorQrCode, ExportadorEmArquivo {

    private static final DateTimeFormatter FORMATO_DATA = ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATO_DATA_HORA = ofPattern("dd/MM/yyyy HH:mm");

    private static final String PREFIXO_ARQUIVO = "comprovante-";
    private static final String EXTENSAO_ARQUIVO = ".txt";

    private final GeradorHashInscricao geradorHash;
    private final EscritorArquivo escritorArquivo;
    private final Path diretorioSaida;

    public EmissorComprovanteDigital(
            GeradorHashInscricao geradorHash,
            EscritorArquivo escritorArquivo,
            @Value("${sge.comprovantes.diretorio:comprovantes}") String diretorioSaida
    ) {
        this.geradorHash = geradorHash;
        this.escritorArquivo = escritorArquivo;
        this.diretorioSaida = Path.of(diretorioSaida);
    }

    @Override
    public TipoComprovante getTipo() {
        return TipoComprovante.DIGITAL_COMPLETO;
    }

    @Override
    public Comprovante emitir(Inscricao inscricao, Evento evento, Participante participante) {
        PayloadQrCode payload = gerarPayloadQrCode(inscricao);
        String conteudo = montarConteudo(inscricao, evento, participante, payload);
        return new ComprovanteDigital(inscricao.getId(), conteudo, payload);
    }

    private String montarConteudo(Inscricao inscricao, Evento evento, Participante participante, PayloadQrCode payload) {
        return """
                COMPROVANTE DIGITAL DE INSCRIÇÃO
                Inscrição: %s
                Data da inscrição: %s
                Participante: %s (%s)
                Evento: %s
                Descrição: %s
                Modalidade: %s
                Data: %s, das %s às %s
                Local: %s
                Código de validação (QR Code): %s
                Payload QR Code: %s"""
                .formatted(inscricao.getId(), inscricao.getCriadoEm().format(FORMATO_DATA_HORA),
                        participante.getNome(), participante.getEmail(),
                        evento.getTitulo(), evento.getDescricao(), evento.getModalidade().getTipo(),
                        evento.getData().format(FORMATO_DATA), evento.getHoraInicio().format(FORMATO_HORA),
                        evento.getHoraFim().format(FORMATO_HORA), evento.getLocal(),
                        payload.hash(),
                        payload.toString());
    }

    @Override
    public PayloadQrCode gerarPayloadQrCode(Inscricao inscricao) {
        String hash = geradorHash.gerar(inscricao.getEventoId(), inscricao.getParticipanteId(), inscricao.getCriadoEm());
        return new PayloadQrCode(hash, inscricao.getId(), inscricao.getEventoId(), inscricao.getParticipanteId());
    }

    @Override
    public Path exportarParaArquivo(Comprovante comprovante) {
        Path destino = diretorioSaida.resolve(nomeDoArquivo(comprovante));
        return escritorArquivo.escrever(destino, comprovante.getConteudo());
    }

    private String nomeDoArquivo(Comprovante comprovante) {
        return PREFIXO_ARQUIVO + comprovante.getInscricaoId() + EXTENSAO_ARQUIVO;
    }
}
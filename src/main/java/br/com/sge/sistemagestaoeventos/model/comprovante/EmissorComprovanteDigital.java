package br.com.sge.sistemagestaoeventos.model.comprovante;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class EmissorComprovanteDigital implements EmissorComprovante {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public TipoComprovante getTipo() {
        return TipoComprovante.DIGITAL_COMPLETO;
    }

    @Override
    public Comprovante emitir(Inscricao inscricao, Evento evento, Participante participante) {
        String conteudo = """
                COMPROVANTE DIGITAL DE INSCRIÇÃO
                Inscrição: %s
                Data da inscrição: %s
                Participante: %s (%s)
                Evento: %s
                Descrição: %s
                Modalidade: %s
                Data: %s, das %s às %s
                Local: %s"""
                .formatted(inscricao.getId(), inscricao.getCriadoEm().format(FORMATO_DATA_HORA),
                        participante.getNome(), participante.getEmail(),
                        evento.getTitulo(), evento.getDescricao(), evento.getModalidade().getTipo(),
                        evento.getData().format(FORMATO_DATA),
                        evento.getHoraInicio().format(FORMATO_HORA),
                        evento.getHoraFim().format(FORMATO_HORA),
                        evento.getLocal());

        return new Comprovante(inscricao.getId(), getTipo(), conteudo);
    }
}

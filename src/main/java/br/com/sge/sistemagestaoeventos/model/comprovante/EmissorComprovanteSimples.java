package br.com.sge.sistemagestaoeventos.model.comprovante;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class EmissorComprovanteSimples implements EmissorComprovante {

    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public TipoComprovante getTipo() {
        return TipoComprovante.SIMPLES;
    }

    @Override
    public Comprovante emitir(Inscricao inscricao, Evento evento, Participante participante) {
        String conteudo = """
                COMPROVANTE DE INSCRIÇÃO
                Inscrição: %s
                Evento: %s
                Participante: %s
                Data da inscrição: %s"""
                .formatted(inscricao.getId(), evento.getTitulo(), participante.getNome(),
                        inscricao.getCriadoEm().format(FORMATO_DATA_HORA));

        return new Comprovante(inscricao.getId(), getTipo(), conteudo);
    }
}

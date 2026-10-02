package br.com.sge.sistemagestaoeventos.model.comprovante;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;

public interface EmissorComprovante {
    TipoComprovante getTipo();
    Comprovante emitir(Inscricao inscricao, Evento evento, Participante participante);
}

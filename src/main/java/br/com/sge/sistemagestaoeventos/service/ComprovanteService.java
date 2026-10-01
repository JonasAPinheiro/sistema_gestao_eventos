package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.factory.comprovante.ComprovanteFactory;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.model.comprovante.Comprovante;
import br.com.sge.sistemagestaoeventos.model.comprovante.EmissorComprovante;
import org.springframework.stereotype.Service;

@Service
public class ComprovanteService {

    private final EventoService eventoService;
    private final ParticipanteService participanteService;
    private final ComprovanteFactory comprovanteFactory;

    public ComprovanteService(
            EventoService eventoService,
            ParticipanteService participanteService,
            ComprovanteFactory comprovanteFactory
    ) {
        this.eventoService = eventoService;
        this.participanteService = participanteService;
        this.comprovanteFactory = comprovanteFactory;
    }

    public Comprovante emitir(Inscricao inscricao) {

        Evento evento = eventoService.buscarPorId(inscricao.getEventoId());
        Participante participante = participanteService.buscarPorId(inscricao.getParticipanteId());
        TipoComprovante tipo = evento.getModalidade().getTipoComprovante();
        EmissorComprovante emissor = comprovanteFactory.obterEmissor(tipo);

        return emissor.emitir(inscricao, evento, participante);
    }
}

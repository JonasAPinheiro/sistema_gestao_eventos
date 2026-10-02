package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.factory.comprovante.ComprovanteFactory;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.model.comprovante.Comprovante;
import br.com.sge.sistemagestaoeventos.model.comprovante.EmissorComprovante;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeAberta;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeComRestricaoDeIdade;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeEvento;
import br.com.sge.sistemagestaoeventos.model.modalidade.ModalidadeExclusivaParaAlunos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComprovanteServiceTest {

    @Mock
    private EventoService eventoService;

    @Mock
    private ParticipanteService participanteService;

    @Mock
    private ComprovanteFactory comprovanteFactory;

    @Mock
    private EmissorComprovante emissorComprovante;

    private ComprovanteService comprovanteService;

    @BeforeEach
    void setUp() {
        comprovanteService = new ComprovanteService(
                eventoService,
                participanteService,
                comprovanteFactory
        );
    }

    @Test
    @DisplayName("Deve emitir comprovante simples para evento aberto")
    void deveEmitirComprovanteSimplesParaEventoAberto() {

        Inscricao inscricao = criarInscricao();
        Evento evento = criarEvento(new ModalidadeAberta());
        Participante participante = criarParticipante();

        Comprovante comprovante = new Comprovante(
                inscricao.getId(),
                TipoComprovante.SIMPLES,
                "COMPROVANTE DE INSCRIÇÃO"
        );

        when(eventoService.buscarPorId("evento-1"))
                .thenReturn(evento);

        when(participanteService.buscarPorId("participante-1"))
                .thenReturn(participante);

        when(comprovanteFactory.obterEmissor(
                TipoComprovante.SIMPLES
        )).thenReturn(emissorComprovante);

        when(emissorComprovante.emitir(
                inscricao,
                evento,
                participante
        )).thenReturn(comprovante);

        Comprovante resultado =
                comprovanteService.emitir(inscricao);

        assertThat(resultado)
                .isSameAs(comprovante);

        verify(eventoService)
                .buscarPorId("evento-1");

        verify(participanteService)
                .buscarPorId("participante-1");

        verify(comprovanteFactory)
                .obterEmissor(TipoComprovante.SIMPLES);

        verify(emissorComprovante)
                .emitir(inscricao, evento, participante);
    }

    @Test
    @DisplayName("Deve emitir comprovante digital para evento exclusivo para alunos")
    void deveEmitirComprovanteDigitalParaEventoExclusivo() {

        Inscricao inscricao = criarInscricao();
        Evento evento = criarEvento(
                new ModalidadeExclusivaParaAlunos()
        );
        Participante participante = criarParticipante();

        Comprovante comprovante = new Comprovante(
                inscricao.getId(),
                TipoComprovante.DIGITAL_COMPLETO,
                "COMPROVANTE DIGITAL DE INSCRIÇÃO"
        );

        when(eventoService.buscarPorId("evento-1"))
                .thenReturn(evento);

        when(participanteService.buscarPorId("participante-1"))
                .thenReturn(participante);

        when(comprovanteFactory.obterEmissor(
                TipoComprovante.DIGITAL_COMPLETO
        )).thenReturn(emissorComprovante);

        when(emissorComprovante.emitir(
                inscricao,
                evento,
                participante
        )).thenReturn(comprovante);

        Comprovante resultado =
                comprovanteService.emitir(inscricao);

        assertThat(resultado)
                .isSameAs(comprovante);

        verify(eventoService)
                .buscarPorId("evento-1");

        verify(participanteService)
                .buscarPorId("participante-1");

        verify(comprovanteFactory)
                .obterEmissor(TipoComprovante.DIGITAL_COMPLETO);

        verify(emissorComprovante)
                .emitir(inscricao, evento, participante);
    }

    @Test
    @DisplayName("Deve emitir comprovante digital para evento com restrição de idade")
    void deveEmitirComprovanteDigitalParaEventoComRestricaoDeIdade() {

        Inscricao inscricao = criarInscricao();
        Evento evento = criarEvento(
                new ModalidadeComRestricaoDeIdade(18)
        );
        Participante participante = criarParticipante();

        Comprovante comprovante = new Comprovante(
                inscricao.getId(),
                TipoComprovante.DIGITAL_COMPLETO,
                "COMPROVANTE DIGITAL DE INSCRIÇÃO"
        );

        when(eventoService.buscarPorId("evento-1"))
                .thenReturn(evento);

        when(participanteService.buscarPorId("participante-1"))
                .thenReturn(participante);

        when(comprovanteFactory.obterEmissor(
                TipoComprovante.DIGITAL_COMPLETO
        )).thenReturn(emissorComprovante);

        when(emissorComprovante.emitir(
                inscricao,
                evento,
                participante
        )).thenReturn(comprovante);

        Comprovante resultado =
                comprovanteService.emitir(inscricao);

        assertThat(resultado)
                .isSameAs(comprovante);

        verify(eventoService)
                .buscarPorId("evento-1");

        verify(participanteService)
                .buscarPorId("participante-1");

        verify(comprovanteFactory)
                .obterEmissor(TipoComprovante.DIGITAL_COMPLETO);

        verify(emissorComprovante)
                .emitir(inscricao, evento, participante);
    }

    private static Inscricao criarInscricao() {
        return new Inscricao(
                "evento-1",
                "participante-1"
        );
    }

    private static Evento criarEvento(
            ModalidadeEvento modalidade
    ) {
        return new Evento(
                "Evento de Teste",
                "Descrição do evento",
                LocalDate.now().plusDays(10),
                LocalTime.of(18, 0),
                LocalTime.of(20, 0),
                "Centro de Eventos",
                100,
                modalidade
        );
    }

    private static Participante criarParticipante() {
        return new Participante(
                "Maria da Silva",
                "maria@email.com"
        );
    }
}

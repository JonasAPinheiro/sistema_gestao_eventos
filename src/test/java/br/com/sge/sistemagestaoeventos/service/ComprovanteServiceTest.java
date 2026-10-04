package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.factory.comprovante.ComprovanteFactory;
import br.com.sge.sistemagestaoeventos.model.Evento;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.model.comprovante.Comprovante;
import br.com.sge.sistemagestaoeventos.model.comprovante.ComprovanteDigital;
import br.com.sge.sistemagestaoeventos.model.comprovante.EmissorComprovante;
import br.com.sge.sistemagestaoeventos.model.comprovante.EmissorComprovanteDigital;
import br.com.sge.sistemagestaoeventos.model.comprovante.EmissorComprovanteSimples;
import br.com.sge.sistemagestaoeventos.model.comprovante.EscritorArquivoEmDisco;
import br.com.sge.sistemagestaoeventos.model.comprovante.GeradorHashInscricaoSha256;
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
import java.util.List;

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

    @Test
    @DisplayName("Deve emitir comprovante simples utilizando emissor real para evento aberto")
    void deveEmitirComprovanteSimplesComEmissorReal() {

        ComprovanteFactory factoryReal = new ComprovanteFactory(
                List.of(new EmissorComprovanteSimples())
        );

        ComprovanteService serviceReal = new ComprovanteService(
                eventoService,
                participanteService,
                factoryReal
        );

        Inscricao inscricao = criarInscricao();
        Evento evento = criarEvento(new ModalidadeAberta());
        Participante participante = criarParticipante();

        when(eventoService.buscarPorId("evento-1"))
                .thenReturn(evento);

        when(participanteService.buscarPorId("participante-1"))
                .thenReturn(participante);

        Comprovante resultado = serviceReal.emitir(inscricao);

        assertThat(resultado.getTipo())
                .isEqualTo(TipoComprovante.SIMPLES);

        assertThat(resultado.getConteudo())
                .contains("COMPROVANTE DE INSCRIÇÃO")
                .contains("Evento: Evento de Teste")
                .contains("Participante: Maria da Silva");

        assertThat(resultado.obterPayloadQrCode())
                .isEmpty();
    }

    @Test
    @DisplayName("Deve emitir comprovante digital completo utilizando emissor real para evento exclusivo")
    void deveEmitirComprovanteDigitalComEmissorReal() {

        ComprovanteFactory factoryReal = new ComprovanteFactory(
                List.of(
                        new EmissorComprovanteDigital(
                                new GeradorHashInscricaoSha256(),
                                new EscritorArquivoEmDisco(),
                                "comprovantes"
                        )
                )
        );

        ComprovanteService serviceReal = new ComprovanteService(
                eventoService,
                participanteService,
                factoryReal
        );

        Inscricao inscricao = criarInscricao();
        Evento evento = criarEvento(new ModalidadeExclusivaParaAlunos());
        Participante participante = criarParticipante();

        when(eventoService.buscarPorId("evento-1"))
                .thenReturn(evento);

        when(participanteService.buscarPorId("participante-1"))
                .thenReturn(participante);

        Comprovante resultado = serviceReal.emitir(inscricao);

        assertThat(resultado)
                .isInstanceOf(ComprovanteDigital.class);

        assertThat(resultado.getTipo())
                .isEqualTo(TipoComprovante.DIGITAL_COMPLETO);

        assertThat(resultado.getConteudo())
                .contains("COMPROVANTE DIGITAL DE INSCRIÇÃO")
                .contains("Descrição: Descrição do evento")
                .contains("Modalidade: EXCLUSIVO_ALUNOS")
                .contains("Código de validação (QR Code):");

        assertThat(resultado.obterPayloadQrCode())
                .isPresent()
                .get()
                .asString()
                .contains("SGE-QR")
                .contains("inscricao=" + inscricao.getId());
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
                "12345",
                "Maria da Silva",
                "maria@email.com",
                LocalDate.of(1995, 1, 1)
        );
    }
}

package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Inscricao;
import br.com.sge.sistemagestaoeventos.model.comprovante.CarregadorRecurso;
import br.com.sge.sistemagestaoeventos.model.comprovante.Comprovante;
import br.com.sge.sistemagestaoeventos.model.comprovante.ExportadorEmArquivo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportacaoComprovanteServiceTest {

    @Mock
    private InscricaoService inscricaoService;

    @Mock
    private ExportadorEmArquivo exportador;

    @Mock
    private CarregadorRecurso carregadorRecurso;

    @Mock
    private Inscricao inscricao;

    @Mock
    private Comprovante comprovante;

    @Mock
    private Resource resource;

    private ExportacaoComprovanteService service;

    @BeforeEach
    void setUp() {

        service = new ExportacaoComprovanteService(
                inscricaoService,
                List.of(exportador),
                carregadorRecurso
        );
    }

    @Test
    @DisplayName("Deve exportar comprovante e retornar recurso")
    void deveExportarComprovante() {

        Path caminho =
                Path.of("comprovantes/comprovante-1.txt");

        when(inscricaoService.consultar(
                "evento-1",
                "participante-1"
        )).thenReturn(inscricao);

        when(inscricao.getComprovante())
                .thenReturn(comprovante);

        when(comprovante.getTipo())
                .thenReturn(TipoComprovante.DIGITAL_COMPLETO);

        when(exportador.getTipo())
                .thenReturn(TipoComprovante.DIGITAL_COMPLETO);

        when(exportador.exportarParaArquivo(comprovante))
                .thenReturn(caminho);

        when(carregadorRecurso.carregar(caminho))
                .thenReturn(resource);

        Resource resultado =
                service.exportar(
                        "evento-1",
                        "participante-1"
                );

        assertThat(resultado)
                .isSameAs(resource);

        verify(exportador)
                .exportarParaArquivo(comprovante);

        verify(carregadorRecurso)
                .carregar(caminho);
    }

    @Test
    @DisplayName("Deve rejeitar inscrição sem comprovante")
    void deveRejeitarInscricaoSemComprovante() {

        when(inscricaoService.consultar(
                "evento-1",
                "participante-1"
        )).thenReturn(inscricao);

        when(inscricao.getComprovante())
                .thenReturn(null);

        assertThatThrownBy(() ->
                service.exportar(
                        "evento-1",
                        "participante-1"
                )
        )
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage(
                        "Esta inscrição ainda não possui um comprovante gerado."
                );

        verifyNoInteractions(exportador);
        verifyNoInteractions(carregadorRecurso);
    }

    @Test
    @DisplayName("Deve rejeitar download de comprovante simples sem exportador compatível")
    void deveRejeitarComprovanteSimplesSemExportador() {

        when(inscricaoService.consultar(
                "evento-1",
                "participante-1"
        )).thenReturn(inscricao);

        when(inscricao.getComprovante())
                .thenReturn(comprovante);

        when(comprovante.getTipo())
                .thenReturn(TipoComprovante.SIMPLES);

        when(exportador.getTipo())
                .thenReturn(TipoComprovante.DIGITAL_COMPLETO);

        assertThatThrownBy(() ->
                service.exportar(
                        "evento-1",
                        "participante-1"
                )
        )
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining(
                        "não suporta download de arquivo"
                );

        verify(exportador, never())
                .exportarParaArquivo(any());

        verifyNoInteractions(carregadorRecurso);
    }

    @Test
    @DisplayName("Deve rejeitar formato sem exportador")
    void deveRejeitarFormatoSemExportador() {

        when(inscricaoService.consultar(
                "evento-1",
                "participante-1"
        )).thenReturn(inscricao);

        when(inscricao.getComprovante())
                .thenReturn(comprovante);

        when(comprovante.getTipo())
                .thenReturn(TipoComprovante.DIGITAL_COMPLETO);

        when(exportador.getTipo())
                .thenReturn(TipoComprovante.SIMPLES);

        assertThatThrownBy(() ->
                service.exportar(
                        "evento-1",
                        "participante-1"
                )
        )
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining(
                        "não suporta download de arquivo"
                );

        verify(exportador, never())
                .exportarParaArquivo(any());

        verifyNoInteractions(carregadorRecurso);
    }
}
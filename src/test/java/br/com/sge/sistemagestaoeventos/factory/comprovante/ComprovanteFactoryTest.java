package br.com.sge.sistemagestaoeventos.factory.comprovante;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import br.com.sge.sistemagestaoeventos.model.comprovante.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ComprovanteFactoryTest {

    private final ComprovanteFactory factory =
            new ComprovanteFactory(
                    List.of(
                            new EmissorComprovanteSimples(),
                            new EmissorComprovanteDigital(new GeradorHashInscricaoSha256(),new EscritorArquivoEmDisco(),"comprovantes")
                    )
            );

    @Test
    @DisplayName("Deve retornar o emissor de comprovante simples")
    void deveRetornarEmissorSimples() {

        EmissorComprovante emissor =
                factory.obterEmissor(TipoComprovante.SIMPLES);

        assertThat(emissor)
                .isInstanceOf(EmissorComprovanteSimples.class);

        assertThat(emissor.getTipo())
                .isEqualTo(TipoComprovante.SIMPLES);
    }

    @Test
    @DisplayName("Deve retornar o emissor de comprovante digital completo")
    void deveRetornarEmissorDigitalCompleto() {

        EmissorComprovante emissor =
                factory.obterEmissor(TipoComprovante.DIGITAL_COMPLETO);

        assertThat(emissor)
                .isInstanceOf(EmissorComprovanteDigital.class);

        assertThat(emissor.getTipo())
                .isEqualTo(TipoComprovante.DIGITAL_COMPLETO);
    }

    @Test
    @DisplayName("Deve disponibilizar estratégias distintas para cada formato de comprovante")
    void deveDisponibilizarEstrategiasDistintasParaCadaFormato() {

        EmissorComprovante emissorSimples =
                factory.obterEmissor(TipoComprovante.SIMPLES);

        EmissorComprovante emissorDigital =
                factory.obterEmissor(TipoComprovante.DIGITAL_COMPLETO);

        assertThat(emissorSimples)
                .isNotSameAs(emissorDigital);

        assertThat(emissorSimples.getTipo())
                .isEqualTo(TipoComprovante.SIMPLES);

        assertThat(emissorDigital.getTipo())
                .isEqualTo(TipoComprovante.DIGITAL_COMPLETO);
    }

    @Test
    @DisplayName("Deve lançar exceção quando não houver emissor configurado")
    void deveLancarExcecaoQuandoNaoHouverEmissor() {

        ComprovanteFactory factorySemEmissor =
                new ComprovanteFactory(
                        List.of(new EmissorComprovanteSimples())
                );

        assertThatThrownBy(() ->
                factorySemEmissor.obterEmissor(
                        TipoComprovante.DIGITAL_COMPLETO
                )
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(
                        "Nenhum emissor de comprovante configurado para o tipo DIGITAL_COMPLETO."
                );
    }
}

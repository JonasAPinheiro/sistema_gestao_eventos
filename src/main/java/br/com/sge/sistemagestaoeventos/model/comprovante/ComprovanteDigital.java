package br.com.sge.sistemagestaoeventos.model.comprovante;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import lombok.Getter;

import java.util.Optional;

@Getter
public class ComprovanteDigital extends Comprovante {

    private final PayloadQrCode payloadQrCode;

    public ComprovanteDigital(String inscricaoId, String conteudo, PayloadQrCode payloadQrCode) {
        super(inscricaoId, TipoComprovante.DIGITAL_COMPLETO, conteudo);
        this.payloadQrCode = payloadQrCode;
    }

    @Override
    public Optional<String> obterPayloadQrCode() {
        return Optional.of(payloadQrCode.toString());
    }
}
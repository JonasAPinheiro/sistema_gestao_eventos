package br.com.sge.sistemagestaoeventos.model.comprovante;

import br.com.sge.sistemagestaoeventos.model.Inscricao;

public interface GeradorQrCode {
    PayloadQrCode gerarPayloadQrCode(Inscricao inscricao);
}

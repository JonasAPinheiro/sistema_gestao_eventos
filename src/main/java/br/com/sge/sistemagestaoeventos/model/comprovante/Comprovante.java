package br.com.sge.sistemagestaoeventos.model.comprovante;

import br.com.sge.sistemagestaoeventos.enums.TipoComprovante;
import lombok.Getter;

@Getter
public class Comprovante {
    private final String inscricaoId;
    private final TipoComprovante tipo;
    private final String conteudo;

    public Comprovante(String inscricaoId, TipoComprovante tipo, String conteudo) {
        this.inscricaoId = inscricaoId;
        this.tipo = tipo;
        this.conteudo = conteudo;
    }
}

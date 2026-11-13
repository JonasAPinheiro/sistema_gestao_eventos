package br.com.sge.sistemagestaoeventos.enums;

public enum TipoLote {
    PROMOCIONAL(10),
    LOTE_1(60),
    LOTE_2(100);

    private final int limitePercentualVendido;

    TipoLote(int limitePercentualVendido) {
        this.limitePercentualVendido = limitePercentualVendido;
    }

    public static TipoLote paraIngressosVendidos(long vendidos, int capacidadeMaxima) {
        for (TipoLote lote : values()) {
            if (vendidos * 100 < (long) lote.limitePercentualVendido * capacidadeMaxima) {
                return lote;
            }
        }
        return LOTE_2;
    }
}

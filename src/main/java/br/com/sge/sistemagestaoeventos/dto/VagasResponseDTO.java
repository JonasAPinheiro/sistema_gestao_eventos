package br.com.sge.sistemagestaoeventos.dto;

public record VagasResponseDTO(
        int capacidadeMaxima,
        long inscricoesConfirmadas,
        long vagasDisponiveis
) {}
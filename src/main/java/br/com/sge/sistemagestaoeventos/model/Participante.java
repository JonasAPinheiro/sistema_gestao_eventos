package br.com.sge.sistemagestaoeventos.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Participante {
    private final String id;
    @Setter
    private String nome;
    @Setter
    private String email;
    private final LocalDateTime dataCriacao;

    public Participante(String nome, String email) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.email = email;
        this.dataCriacao = LocalDateTime.now();
    }
}

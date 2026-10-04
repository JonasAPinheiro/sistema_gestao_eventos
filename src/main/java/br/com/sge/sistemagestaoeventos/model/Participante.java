package br.com.sge.sistemagestaoeventos.model;

import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import lombok.Getter;
import lombok.Setter;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Participante {
    private final String id;
    private final String matricula;
    @Setter
    private String nome;
    @Setter
    private String email;
    private final LocalDate dataNascimento;
    private boolean matriculaAtiva;
    private final LocalDateTime criadoEm;

    public Participante(String matricula, String nome, String email, LocalDate dataNascimento) {
        this.id = UUID.randomUUID().toString();
        this.matricula = matricula;
        this.nome = nome;
        this.email = email;
        this.dataNascimento = dataNascimento;
        this.matriculaAtiva = matricula != null && !matricula.isBlank();
        this.criadoEm = LocalDateTime.now(Clock.systemDefaultZone());
    }

    public void ativarMatricula() {
        if (matricula == null || matricula.isBlank()) {
            throw new RegraNegocioException("Não é possível ativar uma matrícula não informada.");
        }

        this.matriculaAtiva = true;
    }

    public void inativarMatricula() {
        this.matriculaAtiva = false;
    }
}
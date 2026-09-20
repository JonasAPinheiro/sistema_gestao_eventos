package br.com.sge.sistemagestaoeventos.service;

import br.com.sge.sistemagestaoeventos.exception.ParticipanteNaoEncontradoException;
import br.com.sge.sistemagestaoeventos.exception.RegraNegocioException;
import br.com.sge.sistemagestaoeventos.model.Participante;
import br.com.sge.sistemagestaoeventos.repository.ParticipanteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class ParticipanteService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    private final ParticipanteRepository participanteRepository;

    public ParticipanteService(ParticipanteRepository participanteRepository) {
        this.participanteRepository = participanteRepository;
    }

    public Participante buscarPorId(String id) {
        return participanteRepository.buscarPorId(id)
                .orElseThrow(() -> new ParticipanteNaoEncontradoException(id));
    }

    public List<Participante> listarTodos() {
        return participanteRepository.listarTodos();
    }

    public Participante cadastrar(Participante participante) {
        validarDadosDoParticipante(participante);
        validarEmailDisponivelParaCadastro(participante.getEmail());
        return participanteRepository.salvar(participante);
    }

    public Participante atualizar(String id, Participante dadosAtualizados) {
        Participante participanteExistente = buscarPorId(id);
        validarDadosDoParticipante(dadosAtualizados);
        validarEmailDisponivelParaAtualizacao(dadosAtualizados.getEmail(), id);

        participanteExistente.setNome(dadosAtualizados.getNome());
        participanteExistente.setEmail(dadosAtualizados.getEmail());

        return participanteRepository.salvar(participanteExistente);
    }

    private void validarDadosDoParticipante(Participante participante) {
        validarNome(participante.getNome());
        validarEmail(participante.getEmail());
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("O nome do participante é obrigatório.");
        }
    }

    private void validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RegraNegocioException("O e-mail do participante é obrigatório.");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new RegraNegocioException("O e-mail do participante deve possuir um formato válido.");
        }
    }

    private void validarEmailDisponivelParaCadastro(String email) {
        boolean emailJaCadastrado = participanteRepository.buscarPorEmail(email).isPresent();

        if (emailJaCadastrado) {
            throw new RegraNegocioException("Já existe um participante cadastrado com este e-mail.");
        }
    }

    private void validarEmailDisponivelParaAtualizacao(String email, String idAtual) {
        boolean emailPertenceAOutroParticipante = participanteRepository.buscarPorEmail(email)
                .filter(existente -> !existente.getId().equals(idAtual))
                .isPresent();

        if (emailPertenceAOutroParticipante) {
            throw new RegraNegocioException("Já existe um participante cadastrado com este e-mail.");
        }
    }
}

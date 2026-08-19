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
        validarEmailUnico(participante.getEmail(), null);
        return participanteRepository.salvar(participante);
    }

    public Participante atualizar(String id, Participante dadosAtualizados) {
        Participante participanteExistente = buscarPorId(id);
        validarDadosDoParticipante(dadosAtualizados);
        validarEmailUnico(dadosAtualizados.getEmail(), id);

        participanteExistente.setNome(dadosAtualizados.getNome());
        participanteExistente.setEmail(dadosAtualizados.getEmail());

        return participanteRepository.salvar(participanteExistente);
    }

    private void validarDadosDoParticipante(Participante participante) {
        if (participante.getNome() == null || participante.getNome().isBlank()) {
            throw new RegraNegocioException("O nome do participante é obrigatório.");
        }

        if (participante.getEmail() == null || participante.getEmail().isBlank()) {
            throw new RegraNegocioException("O e-mail do participante é obrigatório.");
        }

        if (!EMAIL_PATTERN.matcher(participante.getEmail()).matches()) {
            throw new RegraNegocioException("O e-mail do participante deve possuir um formato válido.");
        }
    }

    private void validarEmailUnico(String email, String idAtual) {
        participanteRepository.buscarPorEmail(email).ifPresent(existente -> {
            if (idAtual == null || !existente.getId().equals(idAtual)) {
                throw new RegraNegocioException("Já existe um participante cadastrado com este e-mail.");
            }
        });
    }
}

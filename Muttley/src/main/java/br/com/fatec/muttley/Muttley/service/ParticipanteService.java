package br.com.fatec.muttley.Muttley.service;

import br.com.fatec.muttley.Muttley.entity.Participante;
import br.com.fatec.muttley.Muttley.enums.TipoParticipante;
import br.com.fatec.muttley.Muttley.repository.ParticipanteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipanteService {

    private final ParticipanteRepository repository;

    public Page<Participante> listar(String busca, TipoParticipante tipo, Pageable pageable) {
        if (tipo != null) {
            return repository.findByTipo(tipo, pageable);
        }
        if (busca != null && !busca.isBlank()) {
            return repository.findByNomeContainingIgnoreCaseOrCpfContaining(busca, busca, pageable);
        }
        return repository.findAll(pageable);
    }

    public Participante buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participante não encontrado"));
    }

    public Optional<Participante> buscarPorCpf(String cpf) {
        return repository.findByCpf(cpf);
    }

    public Participante salvar(Participante participante) {
        if (repository.existsByCpf(participante.getCpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }
        if (repository.existsByEmail(participante.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        return repository.save(participante);
    }

    public Participante atualizar(Long id, Participante dados) {
        Participante existente = buscarPorId(id);

        if (repository.existsByCpfAndIdNot(dados.getCpf(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }
        if (repository.existsByEmailAndIdNot(dados.getEmail(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }

        existente.setNome(dados.getNome());
        existente.setCpf(dados.getCpf());
        existente.setEmail(dados.getEmail());
        existente.setTipo(dados.getTipo());

        return repository.save(existente);
    }
}

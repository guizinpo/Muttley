package br.com.fatec.muttley.Muttley.service;

import br.com.fatec.muttley.Muttley.entity.Palestrante;
import br.com.fatec.muttley.Muttley.repository.PalestranteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PalestranteService {

    private final PalestranteRepository repository;

    public Page<Palestrante> listar(String busca, Pageable pageable) {
        if (busca != null && !busca.isBlank()) {
            return repository.findByNomeContainingIgnoreCase(busca, pageable);
        }
        return repository.findAll(pageable);
    }

    public Palestrante buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Palestrante não encontrado"));
    }

    public Palestrante salvar(Palestrante palestrante) {
        if (repository.existsByEmail(palestrante.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        return repository.save(palestrante);
    }

    public Palestrante atualizar(Long id, Palestrante dados) {
        Palestrante existente = buscarPorId(id);

        if (repository.existsByEmailAndIdNot(dados.getEmail(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }

        existente.setNome(dados.getNome());
        existente.setEmail(dados.getEmail());
        existente.setLinkedin(dados.getLinkedin());
        existente.setEmpresa(dados.getEmpresa());
        existente.setLattes(dados.getLattes());
        existente.setFoto(dados.getFoto());
        existente.setBio(dados.getBio());

        return repository.save(existente);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
    }
}
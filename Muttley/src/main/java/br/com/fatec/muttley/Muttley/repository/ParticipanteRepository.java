package br.com.fatec.muttley.Muttley.repository;

import br.com.fatec.muttley.Muttley.entity.Participante;
import br.com.fatec.muttley.Muttley.enums.TipoParticipante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante, Long> {

    Optional<Participante> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    Page<Participante> findByNomeContainingIgnoreCaseOrCpfContaining(String nome, String cpf, Pageable pageable);

    Page<Participante> findByTipo(TipoParticipante tipo, Pageable pageable);
}

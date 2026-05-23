package br.com.fatec.muttley.Muttley.repository;

import br.com.fatec.muttley.Muttley.entity.Palestrante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PalestranteRepository extends JpaRepository<Palestrante, Long> {

    Optional<Palestrante> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Page<Palestrante> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
package br.com.fatec.muttley.Muttley.repository;

import br.com.fatec.muttley.Muttley.entity.RegrasMedalha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegrasMedalhaRepository extends JpaRepository<RegrasMedalha, Long> {

    Optional<RegrasMedalha> findFirstByOrderByIdAsc();
}

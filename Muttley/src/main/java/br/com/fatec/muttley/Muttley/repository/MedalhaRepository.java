package br.com.fatec.muttley.Muttley.repository;

import br.com.fatec.muttley.Muttley.entity.Medalha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedalhaRepository extends JpaRepository<Medalha, Long> {
    List<Medalha> findAllByOrderByPontosMinAsc();
}
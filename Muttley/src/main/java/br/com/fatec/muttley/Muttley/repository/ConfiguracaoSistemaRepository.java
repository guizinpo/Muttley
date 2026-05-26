package br.com.fatec.muttley.Muttley.repository;

import br.com.fatec.muttley.Muttley.entity.ConfiguracaoSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracaoSistemaRepository extends JpaRepository<ConfiguracaoSistema, Long> {
    Optional<ConfiguracaoSistema> findFirstByOrderByIdAsc();
}
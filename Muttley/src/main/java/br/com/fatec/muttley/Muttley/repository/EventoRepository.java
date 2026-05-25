package br.com.fatec.muttley.Muttley.repository;

import br.com.fatec.muttley.Muttley.entity.Evento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    Optional<Evento> findByQrCodeInscricao(String token);

    Optional<Evento> findByQrCodeParticipacao(String token);

    Page<Evento> findByPalestranteId(Long palestranteId, Pageable pageable);

    @Query("SELECT e FROM Evento e WHERE e.dataEvento >= :hoje ORDER BY e.dataEvento ASC")
    List<Evento> findProximosEventos(LocalDate hoje);

    List<Evento> findByPalestranteId(Long palestranteId);
}

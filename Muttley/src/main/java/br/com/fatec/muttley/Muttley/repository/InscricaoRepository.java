package br.com.fatec.muttley.Muttley.repository;

import br.com.fatec.muttley.Muttley.entity.Inscricao;
import br.com.fatec.muttley.Muttley.enums.StatusInscricao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    boolean existsByParticipanteIdAndEventoId(Long participanteId, Long eventoId);

    Optional<Inscricao> findByParticipanteIdAndEventoId(Long participanteId, Long eventoId);

    Page<Inscricao> findByEventoId(Long eventoId, Pageable pageable);

    @Query("SELECT SUM(i.evento.pontos) FROM Inscricao i " +
            "WHERE i.participante.id = :participanteId " +
            "AND i.status = :status " +
            "AND YEAR(i.dataHoraInscricao) = :ano " +
            "AND (MONTH(i.dataHoraInscricao) <= 6 AND :semestre = 1 " +
            "OR MONTH(i.dataHoraInscricao) > 6 AND :semestre = 2)")
    Double calcularPontosPorSemestre(Long participanteId, StatusInscricao status, int ano, int semestre);

    @Query("SELECT i FROM Inscricao i " +
            "WHERE i.status = :status " +
            "AND YEAR(i.dataHoraInscricao) = :ano " +
            "AND (MONTH(i.dataHoraInscricao) <= 6 AND :semestre = 1 " +
            "OR MONTH(i.dataHoraInscricao) > 6 AND :semestre = 2)")
    List<Inscricao> findByStatusAndSemestre(StatusInscricao status, int ano, int semestre);
}

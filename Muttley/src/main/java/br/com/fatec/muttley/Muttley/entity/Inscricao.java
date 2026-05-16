package br.com.fatec.muttley.Muttley.entity;

import br.com.fatec.muttley.Muttley.enums.StatusInscricao;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inscricoes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"participante_id", "evento_id"})
})
public class Inscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "participante_id", nullable = false)
    private Participante participante;

    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @Column(nullable = false)
    private LocalDateTime dataHoraInscricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusInscricao status;
}

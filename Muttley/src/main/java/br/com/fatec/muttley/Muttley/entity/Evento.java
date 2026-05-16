package br.com.fatec.muttley.Muttley.entity;

import br.com.fatec.muttley.Muttley.enums.TipoEvento;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Tipo é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEvento tipo;

    @NotBlank(message = "Área é obrigatória")
    @Column(nullable = false)
    private String area;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 10, message = "Descrição deve ter no mínimo 10 caracteres")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @NotNull(message = "Palestrante é obrigatório")
    @ManyToOne
    @JoinColumn(name = "palestrante_id", nullable = false)
    private Participante palestrante;

    @NotNull(message = "Pontos é obrigatório")
    @Positive(message = "Pontos deve ser maior que zero")
    @Column(nullable = false)
    private Double pontos;

    @NotNull(message = "Data é obrigatória")
    @Column(nullable = false)
    private LocalDate dataEvento;

    @NotNull(message = "Horário de início é obrigatório")
    @Column(nullable = false)
    private LocalTime horaInicio;

    @NotNull(message = "Horário de fim é obrigatório")
    @Column(nullable = false)
    private LocalTime horaFim;

    @Column(nullable = false, unique = true)
    private String qrCodeInscricao;

    @Column(nullable = false, unique = true)
    private String qrCodeParticipacao;
}

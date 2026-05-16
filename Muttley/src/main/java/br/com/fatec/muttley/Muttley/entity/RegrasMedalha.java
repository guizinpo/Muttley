package br.com.fatec.muttley.Muttley.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "regras_medalha")
public class RegrasMedalha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Mínimo bronze é obrigatório")
    @Column(nullable = false)
    private Double bronzeMin;

    @NotNull(message = "Máximo bronze é obrigatório")
    @Column(nullable = false)
    private Double bronzeMax;

    @NotNull(message = "Mínimo prata é obrigatório")
    @Column(nullable = false)
    private Double prataMin;

    @NotNull(message = "Máximo prata é obrigatório")
    @Column(nullable = false)
    private Double prataMax;

    @NotNull(message = "Mínimo ouro é obrigatório")
    @Column(nullable = false)
    private Double ouroMin;

    @NotNull(message = "Máximo ouro é obrigatório")
    @Column(nullable = false)
    private Double ouroMax;
}

package br.com.fatec.muttley.Muttley.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "medalhas")
public class Medalha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @Column
    private String imagemUrl;

    @NotNull(message = "Pontos mínimos é obrigatório")
    @Column(nullable = false)
    private Double pontosMin;

    @NotNull(message = "Pontos máximos é obrigatório")
    @Column(nullable = false)
    private Double pontosMax;
}
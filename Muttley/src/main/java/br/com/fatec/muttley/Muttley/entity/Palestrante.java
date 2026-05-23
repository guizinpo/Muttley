package br.com.fatec.muttley.Muttley.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "palestrantes")
public class Palestrante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String linkedin;

    @Column
    private String empresa;

    @Column
    private String lattes;

    @Column
    private String foto;

    @Column(columnDefinition = "TEXT")
    private String bio;
}
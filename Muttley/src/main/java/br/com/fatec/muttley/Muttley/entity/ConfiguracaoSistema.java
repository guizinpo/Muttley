package br.com.fatec.muttley.Muttley.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "configuracao_sistema")
public class ConfiguracaoSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean geracaoMedalhasAtiva = false;
}
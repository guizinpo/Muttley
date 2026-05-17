package br.com.fatec.muttley.Muttley.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedalhaResultadoDTO {

    private String participante;
    private String cpf;
    private Double pontos;
    private String medalha;
}

package br.com.fatec.muttley.Muttley.dto;

import br.com.fatec.muttley.Muttley.enums.TipoParticipante;
import lombok.Data;

@Data
public class ParticipanteDetalheDTO {

    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private TipoParticipante tipo;
    private Double pontuacaoSemestre;
    private String medalha;
}

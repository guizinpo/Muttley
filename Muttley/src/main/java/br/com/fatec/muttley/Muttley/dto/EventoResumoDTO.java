package br.com.fatec.muttley.Muttley.dto;

import br.com.fatec.muttley.Muttley.entity.Evento;
import lombok.Data;

@Data
public class EventoResumoDTO {

    private Long id;
    private String tipo;
    private String descricao;
    private String area;
    private String palestrante;
    private String dataEvento;
    private String horaInicio;
    private Double pontos;
    private Long inscritos;

    public static EventoResumoDTO de(Evento evento, Long inscritos) {
        EventoResumoDTO dto = new EventoResumoDTO();
        dto.setId(evento.getId());
        dto.setTipo(evento.getTipo().name());
        dto.setDescricao(evento.getDescricao());
        dto.setArea(evento.getArea());
        dto.setPalestrante(evento.getPalestrante().getNome());
        dto.setDataEvento(evento.getDataEvento().toString());
        dto.setHoraInicio(evento.getHoraInicio().toString());
        dto.setPontos(evento.getPontos());
        dto.setInscritos(inscritos);
        return dto;
    }
}

package br.com.fatec.muttley.Muttley.service;

import br.com.fatec.muttley.Muttley.entity.Inscricao;
import br.com.fatec.muttley.Muttley.entity.Participante;
import br.com.fatec.muttley.Muttley.enums.StatusInscricao;
import br.com.fatec.muttley.Muttley.repository.InscricaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MedalhaService {

    private final InscricaoRepository inscricaoRepository;
    private final RegrasMedalhaService regrasMedalhaService;

    public List<Map<String, Object>> calcularMedalhasSemestre(int ano, int semestre) {
        List<Inscricao> inscricoes = inscricaoRepository
                .findByStatusAndSemestre(StatusInscricao.CONCLUIDO, ano, semestre);

        Map<Participante, Double> pontosPorParticipante = new HashMap<>();

        for (Inscricao inscricao : inscricoes) {
            Participante participante = inscricao.getParticipante();
            Double pontosAtuais = pontosPorParticipante.getOrDefault(participante, 0.0);
            pontosPorParticipante.put(participante, pontosAtuais + inscricao.getEvento().getPontos());
        }

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Map.Entry<Participante, Double> entry : pontosPorParticipante.entrySet()) {
            Participante participante = entry.getKey();
            Double pontos = entry.getValue();
            String medalha = regrasMedalhaService.calcularMedalha(pontos);

            Map<String, Object> item = new HashMap<>();
            item.put("participante", participante.getNome());
            item.put("cpf", participante.getCpf());
            item.put("pontos", pontos);
            item.put("medalha", medalha);

            resultado.add(item);
        }

        resultado.sort((a, b) -> Double.compare(
                (Double) b.get("pontos"),
                (Double) a.get("pontos")));

        return resultado;
    }
}

package br.com.fatec.muttley.Muttley.service;

import br.com.fatec.muttley.Muttley.entity.Inscricao;
import br.com.fatec.muttley.Muttley.entity.Medalha;
import br.com.fatec.muttley.Muttley.entity.Participante;
import br.com.fatec.muttley.Muttley.enums.StatusInscricao;
import br.com.fatec.muttley.Muttley.repository.InscricaoRepository;
import br.com.fatec.muttley.Muttley.repository.MedalhaRepository;
import br.com.fatec.muttley.Muttley.dto.MedalhaResultadoDTO;
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
    private final MedalhaRepository medalhaRepository;
    private final EmailService emailService;
    private final CertificadoService certificadoService;
    private final br.com.fatec.muttley.Muttley.repository.ParticipanteRepository participanteRepository;

    public List<Medalha> listar() {
        return medalhaRepository.findAllByOrderByPontosMinAsc();
    }

    public Medalha buscarPorId(Long id) {
        return medalhaRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Medalha não encontrada"));
    }

    public Medalha salvar(Medalha medalha) {
        validarSobreposicao(medalha, null);
        return medalhaRepository.save(medalha);
    }

    public Medalha atualizar(Long id, Medalha dados) {
        Medalha existente = buscarPorId(id);
        existente.setNome(dados.getNome());
        existente.setImagemUrl(dados.getImagemUrl());
        existente.setPontosMin(dados.getPontosMin());
        existente.setPontosMax(dados.getPontosMax());
        validarSobreposicao(existente, id);
        return medalhaRepository.save(existente);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        medalhaRepository.deleteById(id);
    }

    public String calcularMedalha(Double pontos) {
        List<Medalha> medalhas = medalhaRepository.findAllByOrderByPontosMinAsc();
        for (Medalha medalha : medalhas) {
            if (pontos >= medalha.getPontosMin() && pontos <= medalha.getPontosMax()) {
                return medalha.getNome();
            }
        }
        return null;
    }

    public List<MedalhaResultadoDTO> calcularMedalhasSemestre(int ano, int semestre) {
        List<Inscricao> inscricoes = inscricaoRepository
                .findByStatusAndSemestre(StatusInscricao.CONCLUIDO, ano, semestre);

        Map<Participante, Double> pontosPorParticipante = new HashMap<>();

        for (Inscricao inscricao : inscricoes) {
            Participante participante = inscricao.getParticipante();
            Double pontosAtuais = pontosPorParticipante.getOrDefault(participante, 0.0);
            pontosPorParticipante.put(participante, pontosAtuais + inscricao.getEvento().getPontos());
        }

        List<MedalhaResultadoDTO> resultado = new ArrayList<>();

        for (Map.Entry<Participante, Double> entry : pontosPorParticipante.entrySet()) {
            Participante participante = entry.getKey();
            Double pontos = entry.getValue();
            String medalha = calcularMedalha(pontos);
            resultado.add(new MedalhaResultadoDTO(
                    participante.getNome(),
                    participante.getCpf(),
                    pontos,
                    medalha));
        }

        resultado.sort((a, b) -> Double.compare(b.getPontos(), a.getPontos()));
        return resultado;
    }

    private void validarSobreposicao(Medalha nova, Long idIgnorar) {
        List<Medalha> existentes = medalhaRepository.findAllByOrderByPontosMinAsc();
        for (Medalha m : existentes) {
            if (idIgnorar != null && m.getId().equals(idIgnorar)) continue;
            boolean sobrepos = nova.getPontosMin() <= m.getPontosMax() && nova.getPontosMax() >= m.getPontosMin();
            if (sobrepos) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.CONFLICT,
                        "Faixa de pontos conflita com a medalha '" + m.getNome() + "' (" + m.getPontosMin() + "–" + m.getPontosMax() + " pts)");
            }
        }
    }

    public void enviarCertificadosTodos(int ano, int semestre) {
        List<MedalhaResultadoDTO> resultados = calcularMedalhasSemestre(ano, semestre);

        for (MedalhaResultadoDTO resultado : resultados) {
            if (resultado.getMedalha() == null) continue; // sem medalha, pula

            try {
                Participante participante = participanteRepository.findByCpf(resultado.getCpf())
                        .orElse(null);
                if (participante == null) continue;

                byte[] pdf = certificadoService.gerarCertificadoMedalha(
                        resultado.getParticipante(),
                        resultado.getMedalha(),
                        resultado.getPontos());

                emailService.enviarCertificadoMedalha(participante.getEmail(), participante.getNome(), resultado.getMedalha(), pdf);
            } catch (Exception e) {
                System.err.println("Erro ao enviar certificado de medalha para " +
                        resultado.getParticipante() + ": " + e.getMessage());
            }
        }
    }
}
package br.com.fatec.muttley.Muttley.service;

import br.com.fatec.muttley.Muttley.entity.Evento;
import br.com.fatec.muttley.Muttley.entity.Inscricao;
import br.com.fatec.muttley.Muttley.entity.Participante;
import br.com.fatec.muttley.Muttley.enums.StatusInscricao;
import br.com.fatec.muttley.Muttley.enums.TipoParticipante;
import br.com.fatec.muttley.Muttley.repository.InscricaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InscricaoService {

    private final InscricaoRepository repository;
    private final EventoService eventoService;
    private final ParticipanteService participanteService;

    public Page<Inscricao> listarPorEvento(Long eventoId, String busca, Pageable pageable) {
        eventoService.buscarPorId(eventoId);
        if (busca != null && !busca.isBlank()) {
            return repository.findByEventoIdAndBusca(eventoId, busca, pageable);
        }
        return repository.findByEventoId(eventoId, pageable);
    }

    public Double calcularPontos(Long participanteId, int ano, int semestre) {
        Double pontos = repository.calcularPontosPorSemestre(
                participanteId, StatusInscricao.CONCLUIDO, ano, semestre);
        return pontos != null ? pontos : 0.0;
    }

    public Inscricao inscreverViaQrCode(String token, Participante dadosParticipante) {
        Evento evento = eventoService.buscarPorQrCodeInscricao(token);

        if (dadosParticipante.getTipo() == TipoParticipante.PALESTRANTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Palestrantes não podem se inscrever como ouvintes");
        }

        Participante participante = participanteService.buscarPorCpf(dadosParticipante.getCpf())
                .orElseGet(() -> participanteService.salvar(dadosParticipante));

        if (repository.existsByParticipanteIdAndEventoId(participante.getId(), evento.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Participante já inscrito neste evento");
        }

        Inscricao inscricao = new Inscricao();
        inscricao.setParticipante(participante);
        inscricao.setEvento(evento);
        inscricao.setDataHoraInscricao(LocalDateTime.now());
        inscricao.setStatus(StatusInscricao.AGENDADO);

        return repository.save(inscricao);
    }

    public Inscricao confirmarPresenca(String token, String cpf){

        Evento evento = eventoService.buscarPorQrCodeParticipacao(token);

        Participante participante = participanteService.buscarPorCpf(cpf)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Participante não encontrado"));

        Inscricao inscricao = repository
                .findByParticipanteIdAndEventoId(participante.getId(), evento.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Inscrição não encontrada para este evento"));

        if (inscricao.getStatus() == StatusInscricao.CONCLUIDO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Presença já confirmada");
        }

        inscricao.setStatus(StatusInscricao.CONCLUIDO);
        return repository.save(inscricao);
    }

    public Page<Inscricao> listarPorParticipante(Long participanteId, Pageable pageable) {
        participanteService.buscarPorId(participanteId);
        return repository.findByParticipanteId(participanteId, pageable);
    }
}

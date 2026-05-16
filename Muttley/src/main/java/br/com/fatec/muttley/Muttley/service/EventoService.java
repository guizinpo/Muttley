package br.com.fatec.muttley.Muttley.service;

import br.com.fatec.muttley.Muttley.entity.Evento;
import br.com.fatec.muttley.Muttley.enums.TipoEvento;
import br.com.fatec.muttley.Muttley.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository repository;

    public List<Evento> listarProximos() {
        return repository.findProximosEventos(LocalDate.now());
    }

    public Page<Evento> listar(TipoEvento tipo, Pageable pageable) {
        if (tipo != null) {
            return repository.findByTipo(tipo, pageable);
        }
        return repository.findAll(pageable);
    }

    public Evento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
    }

    public Evento buscarPorQrCodeInscricao(String token) {
        return repository.findByQrCodeInscricao(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "QR Code inválido ou evento encerrado"));
    }

    public Evento buscarPorQrCodeParticipacao(String token) {
        return repository.findByQrCodeParticipacao(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "QR Code inválido ou evento encerrado"));
    }

    public Evento salvar(Evento evento) {
        validarHorarios(evento);
        evento.setQrCodeInscricao(UUID.randomUUID().toString());
        evento.setQrCodeParticipacao(UUID.randomUUID().toString());
        return repository.save(evento);
    }

    public Evento atualizar(Long id, Evento dados) {
        Evento existente = buscarPorId(id);
        validarHorarios(dados);

        existente.setTipo(dados.getTipo());
        existente.setArea(dados.getArea());
        existente.setDescricao(dados.getDescricao());
        existente.setPalestrante(dados.getPalestrante());
        existente.setPontos(dados.getPontos());
        existente.setDataEvento(dados.getDataEvento());
        existente.setHoraInicio(dados.getHoraInicio());
        existente.setHoraFim(dados.getHoraFim());

        return repository.save(existente);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
    }

    private void validarHorarios(Evento evento) {
        if (evento.getHoraFim().isBefore(evento.getHoraInicio()) ||
                evento.getHoraFim().equals(evento.getHoraInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Horário de fim deve ser após o horário de início");
        }
    }
}

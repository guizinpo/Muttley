package br.com.fatec.muttley.Muttley.controller;

import br.com.fatec.muttley.Muttley.dto.EventoResumoDTO;
import br.com.fatec.muttley.Muttley.entity.Evento;
import br.com.fatec.muttley.Muttley.repository.InscricaoRepository;
import br.com.fatec.muttley.Muttley.service.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final EventoService service;
    private final InscricaoRepository inscricaoRepository;

    @GetMapping
    public ResponseEntity<List<EventoResumoDTO>> proximosEventos() {
        List<Evento> eventos = service.listarProximos();
        List<EventoResumoDTO> resultado = eventos.stream()
                .map(evento -> {
                    long inscritos = inscricaoRepository.countByEventoId(evento.getId());
                    return EventoResumoDTO.de(evento, inscritos);
                })
                .toList();
        return ResponseEntity.ok(resultado);
    }
}
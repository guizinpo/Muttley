package br.com.fatec.muttley.Muttley.controller;

import br.com.fatec.muttley.Muttley.entity.Inscricao;
import br.com.fatec.muttley.Muttley.service.InscricaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inscricoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InscricaoController {

    private final InscricaoService service;

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<Page<Inscricao>> listarPorEvento(
            @PathVariable Long eventoId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.listarPorEvento(eventoId, pageable));
    }

    @GetMapping("/participante/{participanteId}/pontos")
    public ResponseEntity<Double> calcularPontos(
            @PathVariable Long participanteId,
            @RequestParam int ano,
            @RequestParam int semestre) {
        return ResponseEntity.ok(service.calcularPontos(participanteId, ano, semestre));
    }
}

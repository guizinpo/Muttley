package br.com.fatec.muttley.Muttley.controller;

import br.com.fatec.muttley.Muttley.entity.Participante;
import br.com.fatec.muttley.Muttley.enums.TipoParticipante;
import br.com.fatec.muttley.Muttley.service.ParticipanteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/participantes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ParticipanteController {
    private final ParticipanteService service;

    @GetMapping
    public ResponseEntity<Page<Participante>> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) TipoParticipante tipo,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(busca, tipo, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participante> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Participante> salvar(@RequestBody @Valid Participante participante) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(participante));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Participante> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid Participante participante) {
        return ResponseEntity.ok(service.atualizar(id, participante));
    }
}

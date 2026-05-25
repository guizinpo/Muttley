package br.com.fatec.muttley.Muttley.controller;

import br.com.fatec.muttley.Muttley.entity.Palestrante;
import br.com.fatec.muttley.Muttley.service.EmailService;
import br.com.fatec.muttley.Muttley.service.PalestranteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/palestrantes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PalestranteController {

    private final PalestranteService service;
    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<Page<Palestrante>> listar(
            @RequestParam(required = false) String busca,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(busca, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Palestrante> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Palestrante> salvar(@RequestBody @Valid Palestrante palestrante) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(palestrante));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Palestrante> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid Palestrante palestrante) {
        return ResponseEntity.ok(service.atualizar(id, palestrante));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enviar-certificado")
    public ResponseEntity<Void> enviarCertificado(@PathVariable Long id) {
        emailService.enviarCertificadoPalestrante(id);
        return ResponseEntity.ok().build();
    }
}
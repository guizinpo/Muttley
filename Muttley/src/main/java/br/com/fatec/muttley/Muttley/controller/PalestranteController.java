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

import java.util.Map;

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

    @PostMapping("/upload-foto")
    public ResponseEntity<Map<String, String>> uploadFoto(
            @RequestParam("arquivo") org.springframework.web.multipart.MultipartFile arquivo) {
        try {
            String nomeArquivo = System.currentTimeMillis() + "_" + arquivo.getOriginalFilename();
            String pastaUploads = System.getProperty("user.dir") + "/uploads/palestrantes/";
            java.nio.file.Path destino = java.nio.file.Paths.get(pastaUploads + nomeArquivo);
            java.nio.file.Files.createDirectories(destino.getParent());
            arquivo.transferTo(destino.toFile());
            String url = "/uploads/palestrantes/" + nomeArquivo;
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload da foto", e);
        }
    }
}
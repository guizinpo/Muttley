package br.com.fatec.muttley.Muttley.controller;

import br.com.fatec.muttley.Muttley.entity.Inscricao;
import br.com.fatec.muttley.Muttley.service.CertificadoService;
import br.com.fatec.muttley.Muttley.service.EmailService;
import br.com.fatec.muttley.Muttley.service.InscricaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/inscricoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InscricaoController {

    private final InscricaoService service;
    private final CertificadoService certificadoService;
    private final EmailService emailService;

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<Page<Inscricao>> listarPorEvento(
            @PathVariable Long eventoId,
            @RequestParam(required = false) String busca,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.listarPorEvento(eventoId, busca, pageable));
    }

    @GetMapping("/participante/{participanteId}/pontos")
    public ResponseEntity<Double> calcularPontos(
            @PathVariable Long participanteId,
            @RequestParam int ano,
            @RequestParam int semestre) {
        return ResponseEntity.ok(service.calcularPontos(participanteId, ano, semestre));
    }

    @GetMapping("/{id}/certificado")
    public ResponseEntity<byte[]> gerarCertificado(@PathVariable Long id) {
        byte[] pdf = certificadoService.gerarCertificado(id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=certificado.pdf")
                .body(pdf);
    }

    @PostMapping("/{id}/enviar-certificado")
    public ResponseEntity<Void> enviarCertificado(@PathVariable Long id) throws Exception {
        emailService.enviarCertificado(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/participante/{participanteId}")
    public ResponseEntity<Page<Inscricao>> listarPorParticipante(
            @PathVariable Long participanteId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.listarPorParticipante(participanteId, pageable));
    }
}

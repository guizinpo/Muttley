package br.com.fatec.muttley.Muttley.controller;

import br.com.fatec.muttley.Muttley.entity.Evento;
import br.com.fatec.muttley.Muttley.enums.TipoEvento;
import br.com.fatec.muttley.Muttley.service.EventoService;
import br.com.fatec.muttley.Muttley.service.QrCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventoController {

    private final EventoService service;
    private final QrCodeService qrCodeService;

    @GetMapping("/proximos")
    public ResponseEntity<List<Evento>> listarProximos() {
        return ResponseEntity.ok(service.listarProximos());
    }

    @GetMapping
    public ResponseEntity<Page<Evento>> listar(
            @RequestParam(required = false) TipoEvento tipo,
            @PageableDefault(size = 10, sort = "dataEvento") Pageable pageable) {
        return ResponseEntity.ok(service.listar(tipo, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Evento> salvar(@RequestBody @Valid Evento evento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(evento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid Evento evento) {
        return ResponseEntity.ok(service.atualizar(id, evento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/qrcode/inscricao")
    public ResponseEntity<byte[]> qrCodeInscricao(
            @PathVariable Long id,
            @RequestParam String baseUrl) {
        Evento evento = service.buscarPorId(id);
        String url = baseUrl + "/inscricao/" + evento.getQrCodeInscricao();
        byte[] imagem = qrCodeService.gerarQrCode(url);
        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(imagem);
    }

    @GetMapping("/{id}/qrcode/participacao")
    public ResponseEntity<byte[]> qrCodeParticipacao(
            @PathVariable Long id,
            @RequestParam String baseUrl) {
        Evento evento = service.buscarPorId(id);
        String url = baseUrl + "/participacao/" + evento.getQrCodeParticipacao();
        byte[] imagem = qrCodeService.gerarQrCode(url);
        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(imagem);
    }
}

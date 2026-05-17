package br.com.fatec.muttley.Muttley.controller;

import br.com.fatec.muttley.Muttley.entity.Evento;
import br.com.fatec.muttley.Muttley.entity.Inscricao;
import br.com.fatec.muttley.Muttley.entity.Participante;
import br.com.fatec.muttley.Muttley.service.EventoService;
import br.com.fatec.muttley.Muttley.service.InscricaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.fatec.muttley.Muttley.dto.InscricaoPublicaDTO;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PublicInscricaoController {

    private final InscricaoService inscricaoService;
    private final EventoService eventoService;

    @GetMapping("/inscricao/{token}")
    public ResponseEntity<Evento> buscarEventoPorToken(@PathVariable String token) {
        return ResponseEntity.ok(eventoService.buscarPorQrCodeInscricao(token));
    }

    @PostMapping("/inscricao/{token}")
    public ResponseEntity<Inscricao> inscrever(
            @PathVariable String token,
            @RequestBody @Valid InscricaoPublicaDTO dto) {

        Participante participante = new Participante();
        participante.setNome(dto.getNome());
        participante.setCpf(dto.getCpf());
        participante.setEmail(dto.getEmail());
        participante.setTipo(dto.getTipo());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inscricaoService.inscreverViaQrCode(token, participante));
    }

    @GetMapping("/participacao/{token}")
    public ResponseEntity<Evento> buscarEventoPorTokenParticipacao(@PathVariable String token) {
        return ResponseEntity.ok(eventoService.buscarPorQrCodeParticipacao(token));
    }

    @PostMapping("/participacao/{token}")
    public ResponseEntity<Inscricao> confirmarPresenca(
            @PathVariable String token,
            @RequestParam String cpf) {
        return ResponseEntity.ok(inscricaoService.confirmarPresenca(token, cpf));
    }
}

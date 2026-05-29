package br.com.fatec.muttley.Muttley.controller;

import br.com.fatec.muttley.Muttley.entity.Evento;
import br.com.fatec.muttley.Muttley.entity.Inscricao;
import br.com.fatec.muttley.Muttley.entity.Participante;
import br.com.fatec.muttley.Muttley.service.CertificadoService;
import br.com.fatec.muttley.Muttley.service.EventoService;
import br.com.fatec.muttley.Muttley.service.InscricaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.fatec.muttley.Muttley.dto.InscricaoPublicaDTO;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PublicInscricaoController {

    private final InscricaoService inscricaoService;
    private final EventoService eventoService;
    private final CertificadoService certificadoService;

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

    @GetMapping("/certificados")
    public ResponseEntity<List<Inscricao>> buscarCertificados(
            @RequestParam String cpf) {
        return ResponseEntity.ok(inscricaoService.buscarConcluidosPorCpf(cpf));
    }

    @GetMapping("/certificados/{inscricaoId}/download")
    public ResponseEntity<byte[]> baixarCertificado(
            @PathVariable Long inscricaoId,
            @RequestParam String cpf) {
        // valida que o cpf é dono da inscrição
        Inscricao inscricao = inscricaoService.buscarPorIdECpf(inscricaoId, cpf);
        byte[] pdf = certificadoService.gerarCertificado(inscricao.getId());
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=certificado.pdf")
                .body(pdf);
    }

    @PostMapping("/certificados/enviar")
    public ResponseEntity<Void> enviarTodos(@RequestParam String cpf) {
        inscricaoService.enviarTodosCertificados(cpf);
        return ResponseEntity.ok().build();
    }
}

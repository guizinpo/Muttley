package br.com.fatec.muttley.Muttley.controller;

import br.com.fatec.muttley.Muttley.entity.RegrasMedalha;
import br.com.fatec.muttley.Muttley.service.MedalhaService;
import br.com.fatec.muttley.Muttley.service.RegrasMedalhaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.fatec.muttley.Muttley.dto.MedalhaResultadoDTO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medalhas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MedalhaController {

    private final RegrasMedalhaService regrasMedalhaService;
    private final MedalhaService medalhaService;

    @GetMapping("/regras")
    public ResponseEntity<RegrasMedalha> buscarRegras() {
        return ResponseEntity.ok(regrasMedalhaService.buscar());
    }

    @PutMapping("/regras")
    public ResponseEntity<RegrasMedalha> salvarRegras(@RequestBody @Valid RegrasMedalha regras) {
        return ResponseEntity.ok(regrasMedalhaService.salvar(regras));
    }

    @PostMapping("/regras/restaurar")
    public ResponseEntity<RegrasMedalha> restaurarPadrao() {
        return ResponseEntity.ok(regrasMedalhaService.restaurarPadrao());
    }

    @GetMapping("/calcular")
    public ResponseEntity<List<MedalhaResultadoDTO>> calcular(
            @RequestParam int ano,
            @RequestParam int semestre) {
        return ResponseEntity.ok(medalhaService.calcularMedalhasSemestre(ano, semestre));
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarCsv(
            @RequestParam int ano,
            @RequestParam int semestre) {

        List<MedalhaResultadoDTO> resultado = medalhaService.calcularMedalhasSemestre(ano, semestre);

        StringBuilder csv = new StringBuilder();
        csv.append("Participante,CPF,Pontos,Medalha\n");

        for (MedalhaResultadoDTO item : resultado) {
            csv.append(item.getParticipante()).append(",")
                    .append(item.getCpf()).append(",")
                    .append(item.getPontos()).append(",")
                    .append(item.getMedalha() != null ? item.getMedalha() : "Sem medalha")
                    .append("\n");
        }

        byte[] bytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header("Content-Type", "text/csv; charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=medalhas_" + semestre + "_" + ano + ".csv")
                .body(bytes);
    }
}

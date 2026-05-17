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
}

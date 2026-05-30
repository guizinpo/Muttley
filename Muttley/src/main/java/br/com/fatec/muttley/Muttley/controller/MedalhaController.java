package br.com.fatec.muttley.Muttley.controller;

import br.com.fatec.muttley.Muttley.entity.Medalha;
import br.com.fatec.muttley.Muttley.service.ConfiguracaoSistemaService;
import br.com.fatec.muttley.Muttley.service.MedalhaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.fatec.muttley.Muttley.dto.MedalhaResultadoDTO;

import java.util.List;

@RestController
@RequestMapping("/api/medalhas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MedalhaController {

    private final MedalhaService medalhaService;
    private final ConfiguracaoSistemaService configuracaoService;

    @GetMapping
    public ResponseEntity<List<Medalha>> listar() {
        return ResponseEntity.ok(medalhaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medalha> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medalhaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Medalha> salvar(@RequestBody @Valid Medalha medalha) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medalhaService.salvar(medalha));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medalha> atualizar(@PathVariable Long id,
                                             @RequestBody @Valid Medalha medalha) {
        return ResponseEntity.ok(medalhaService.atualizar(id, medalha));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        medalhaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImagem(
            @RequestParam("arquivo") org.springframework.web.multipart.MultipartFile arquivo) {
        try {
            String nomeArquivo = java.util.UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
            String pastaUploads = System.getProperty("user.dir") + "/uploads/medalhas/";
            java.nio.file.Path destino = java.nio.file.Paths.get(pastaUploads + nomeArquivo);
            java.nio.file.Files.createDirectories(destino.getParent());
            arquivo.transferTo(destino.toFile());
            String url = "/uploads/medalhas/" + nomeArquivo;
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao fazer upload: " + e.getMessage());
        }
    }

    @GetMapping("/calcular")
    public ResponseEntity<List<MedalhaResultadoDTO>> calcular(
            @RequestParam int ano,
            @RequestParam int semestre) {
        if (!configuracaoService.isGeracaoMedalhasAtiva()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(medalhaService.calcularMedalhasSemestre(ano, semestre));
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarCsv(
            @RequestParam int ano,
            @RequestParam int semestre) {

        List<MedalhaResultadoDTO> resultado = medalhaService.calcularMedalhasSemestre(ano, semestre);

        StringBuilder csv = new StringBuilder();
        csv.append("RELATÓRIO DE MEDALHAS\n");
        csv.append("Semestre:,").append(semestre).append("º / ").append(ano).append("\n");
        csv.append("Total de participantes:,").append(resultado.size()).append("\n");
        csv.append("\n");
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

    @PostMapping("/enviar-certificados")
    public ResponseEntity<Void> enviarCertificados(
            @RequestParam int ano,
            @RequestParam int semestre) {
        medalhaService.enviarCertificadosTodos(ano, semestre);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/configuracao")
    public ResponseEntity<Boolean> verificarGeracaoAtiva() {
        return ResponseEntity.ok(configuracaoService.isGeracaoMedalhasAtiva());
    }

    @PostMapping("/configuracao/ativar")
    public ResponseEntity<Void> ativarGeracao() {
        configuracaoService.ativarGeracaoMedalhas();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/configuracao/desativar")
    public ResponseEntity<Void> desativarGeracao() {
        configuracaoService.desativarGeracaoMedalhas();
        return ResponseEntity.ok().build();
    }
}
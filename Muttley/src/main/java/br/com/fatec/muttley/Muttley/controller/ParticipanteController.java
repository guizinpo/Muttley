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
import br.com.fatec.muttley.Muttley.dto.ParticipanteDetalheDTO;
import br.com.fatec.muttley.Muttley.service.InscricaoService;
import br.com.fatec.muttley.Muttley.service.MedalhaService;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/participantes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ParticipanteController {
    private final ParticipanteService service;

    private final InscricaoService inscricaoService;
    private final MedalhaService medalhaService;

    @GetMapping
    public ResponseEntity<Page<Participante>> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) TipoParticipante tipo,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(busca, tipo, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipanteDetalheDTO> buscarPorId(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "0") int ano,
            @RequestParam(required = false, defaultValue = "0") int semestre) {

        Participante participante = service.buscarPorId(id);

        int anoConsulta = ano == 0 ? LocalDate.now().getYear() : ano;
        int semestreConsulta = semestre == 0 ? (LocalDate.now().getMonthValue() <= 6 ? 1 : 2) : semestre;

        Double pontos = inscricaoService.calcularPontos(participante.getId(), anoConsulta, semestreConsulta);
        String medalha = medalhaService.calcularMedalha(pontos);

        ParticipanteDetalheDTO dto = new ParticipanteDetalheDTO();
        dto.setId(participante.getId());
        dto.setNome(participante.getNome());
        dto.setCpf(participante.getCpf());
        dto.setEmail(participante.getEmail());
        dto.setTipo(participante.getTipo());
        dto.setPontuacaoSemestre(pontos);
        dto.setMedalha(medalha);

        return ResponseEntity.ok(dto);
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

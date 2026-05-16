package br.com.fatec.muttley.Muttley.controller;

import br.com.fatec.muttley.Muttley.entity.Evento;
import br.com.fatec.muttley.Muttley.service.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final EventoService service;

    @GetMapping
    public ResponseEntity<List<Evento>> proximosEventos() {
        return ResponseEntity.ok(service.listarProximos());
    }
}

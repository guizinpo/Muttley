package br.com.fatec.muttley.Muttley.service;

import br.com.fatec.muttley.Muttley.entity.RegrasMedalha;
import br.com.fatec.muttley.Muttley.repository.RegrasMedalhaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RegrasMedalhaService {

    private final RegrasMedalhaRepository repository;

    private static final Double BRONZE_MIN_PADRAO = 1.0;
    private static final Double BRONZE_MAX_PADRAO = 4.0;
    private static final Double PRATA_MIN_PADRAO = 5.0;
    private static final Double PRATA_MAX_PADRAO = 8.0;
    private static final Double OURO_MIN_PADRAO = 9.0;
    private static final Double OURO_MAX_PADRAO = 12.0;

    public RegrasMedalha buscar() {
        return repository.findFirstByOrderByIdAsc()
                .orElseGet(this::criarPadrao);
    }

    public RegrasMedalha salvar(RegrasMedalha regras) {
        validarRegras(regras);
        RegrasMedalha existente = repository.findFirstByOrderByIdAsc()
                .orElse(new RegrasMedalha());
        existente.setBronzeMin(regras.getBronzeMin());
        existente.setBronzeMax(regras.getBronzeMax());
        existente.setPrataMin(regras.getPrataMin());
        existente.setPrataMax(regras.getPrataMax());
        existente.setOuroMin(regras.getOuroMin());
        existente.setOuroMax(regras.getOuroMax());
        return repository.save(existente);
    }

    public RegrasMedalha restaurarPadrao() {
        RegrasMedalha existente = repository.findFirstByOrderByIdAsc()
                .orElse(new RegrasMedalha());
        existente.setBronzeMin(BRONZE_MIN_PADRAO);
        existente.setBronzeMax(BRONZE_MAX_PADRAO);
        existente.setPrataMin(PRATA_MIN_PADRAO);
        existente.setPrataMax(PRATA_MAX_PADRAO);
        existente.setOuroMin(OURO_MIN_PADRAO);
        existente.setOuroMax(OURO_MAX_PADRAO);
        return repository.save(existente);
    }

    public String calcularMedalha(Double pontos) {
        RegrasMedalha regras = buscar();
        if (pontos >= regras.getOuroMin() && pontos <= regras.getOuroMax()) {
            return "OURO";
        } else if (pontos >= regras.getPrataMin() && pontos <= regras.getPrataMax()) {
            return "PRATA";
        } else if (pontos >= regras.getBronzeMin() && pontos <= regras.getBronzeMax()) {
            return "BRONZE";
        }
        return null;
    }

    private RegrasMedalha criarPadrao() {
        RegrasMedalha padrao = new RegrasMedalha();
        padrao.setBronzeMin(BRONZE_MIN_PADRAO);
        padrao.setBronzeMax(BRONZE_MAX_PADRAO);
        padrao.setPrataMin(PRATA_MIN_PADRAO);
        padrao.setPrataMax(PRATA_MAX_PADRAO);
        padrao.setOuroMin(OURO_MIN_PADRAO);
        padrao.setOuroMax(OURO_MAX_PADRAO);
        return repository.save(padrao);
    }

    private void validarRegras(RegrasMedalha regras) {
        if (regras.getBronzeMin() >= regras.getBronzeMax()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mínimo bronze deve ser menor que máximo bronze");
        }
        if (regras.getPrataMin() >= regras.getPrataMax()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mínimo prata deve ser menor que máximo prata");
        }
        if (regras.getOuroMin() >= regras.getOuroMax()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mínimo ouro deve ser menor que máximo ouro");
        }
        if (regras.getBronzeMax() >= regras.getPrataMin()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Faixas de pontos não podem se sobrepor");
        }
        if (regras.getPrataMax() >= regras.getOuroMin()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Faixas de pontos não podem se sobrepor");
        }
    }
}

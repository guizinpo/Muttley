package br.com.fatec.muttley.Muttley.service;

import br.com.fatec.muttley.Muttley.entity.ConfiguracaoSistema;
import br.com.fatec.muttley.Muttley.repository.ConfiguracaoSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;

import java.time.Month;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfiguracaoSistemaService {

    private final ConfiguracaoSistemaRepository repository;

    public ConfiguracaoSistema buscar() {
        return repository.findFirstByOrderByIdAsc()
                .orElseGet(this::criarPadrao);
    }

    @Value("${medalhas.geracao.meses-permitidos:6,12}")
    private String mesesPermitidos;

    public ConfiguracaoSistema ativarGeracaoMedalhas() {
        int mesAtual = java.time.LocalDate.now().getMonthValue();
        List<Integer> meses = Arrays.stream(mesesPermitidos.split(","))
                .map(s -> Integer.parseInt(s.trim()))
                .toList();
        if (!meses.contains(mesAtual)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Ativação permitida apenas nos meses: " + mesesPermitidos);
        }
        ConfiguracaoSistema config = buscar();
        config.setGeracaoMedalhasAtiva(true);
        return repository.save(config);
    }

    public ConfiguracaoSistema desativarGeracaoMedalhas() {
        ConfiguracaoSistema config = buscar();
        config.setGeracaoMedalhasAtiva(false);
        return repository.save(config);
    }

    public boolean isGeracaoMedalhasAtiva() {
        return buscar().getGeracaoMedalhasAtiva();
    }

    private ConfiguracaoSistema criarPadrao() {
        ConfiguracaoSistema config = new ConfiguracaoSistema();
        config.setGeracaoMedalhasAtiva(false);
        return repository.save(config);
    }
}
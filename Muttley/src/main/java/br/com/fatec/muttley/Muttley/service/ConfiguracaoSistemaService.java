package br.com.fatec.muttley.Muttley.service;

import br.com.fatec.muttley.Muttley.entity.ConfiguracaoSistema;
import br.com.fatec.muttley.Muttley.repository.ConfiguracaoSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfiguracaoSistemaService {

    private final ConfiguracaoSistemaRepository repository;

    public ConfiguracaoSistema buscar() {
        return repository.findFirstByOrderByIdAsc()
                .orElseGet(this::criarPadrao);
    }

    public ConfiguracaoSistema ativarGeracaoMedalhas() {
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
package com.meuprojeto.apipaises.service;

import com.meuprojeto.apipaises.model.Pais;
import com.meuprojeto.apipaises.repository.PaisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PaisService {

    @Autowired
    private PaisRepository paisRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public void importarPaises() {
        String url = "https://restcountries.com/v3.1/all";
        Pais[] paisesExternos = restTemplate.getForObject(url, Pais[].class);
         if (paisesExternos != null) {
            paisRepository.saveAll(Arrays.asList(paisesExternos));
//            Arrays.stream(paisesExternos).forEach(paisRepository::save);
        }
    }

    public List<Pais> listarTodos() {
        return paisRepository.findAll();
    }

    public Optional<Pais> buscarPorId(Long id) {
        return paisRepository.findById(id);
    }

    public Optional<Pais> atualizar(Long id, Pais paisAtualizado) {
        return paisRepository.findById(id).map(paisExistente -> {
            paisExistente.setNome(paisAtualizado.getNome());
            paisExistente.setRegiao(paisAtualizado.getRegiao());
            paisExistente.setPopulacao(paisAtualizado.getPopulacao());
            paisExistente.setCapital(paisAtualizado.getCapital());
            return paisRepository.save(paisExistente);
        });
    }

    public boolean deletar(Long id) {
        if (paisRepository.existsById(id)) {
            paisRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
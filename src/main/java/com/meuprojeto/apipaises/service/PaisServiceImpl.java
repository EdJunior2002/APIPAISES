package com.meuprojeto.apipaises.service;

import com.meuprojeto.apipaises.model.Pais;
import com.meuprojeto.apipaises.repository.PaisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PaisServiceImpl extends PaisService {

    @Autowired
    private PaisRepository paisRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void importarPaises() {
        String url = "https://restcountries.com/v3.1/all";
        Pais[] paisesExternos = restTemplate.getForObject(url, Pais[].class);
        if (paisesExternos != null) {
            paisRepository.saveAll(Arrays.asList(paisesExternos));
        }
    }

    @Override
    public List<Pais> listarTodos() {
        return paisRepository.findAll();
    }

    @Override
    public Optional<Pais> buscarPorId(Long id) {
        return paisRepository.findById(id);
    }

    @Override
    public Optional<Pais> atualizar(Long id, Pais paisAtualizado) {
        return paisRepository.findById(id).map(paisExistente -> {
            paisExistente.setNome(paisAtualizado.getNome());
            paisExistente.setRegiao(paisAtualizado.getRegiao());
            paisExistente.setPopulacao(paisAtualizado.getPopulacao());
            paisExistente.setCapital(paisAtualizado.getCapital());
            return paisRepository.save(paisExistente);
        });
    }

    @Override
    public boolean deletar(Long id) {
        if (paisRepository.existsById(id)) {
            paisRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Page<Pais> listarComFiltros(String regiao, Long populacaoMaiorQue, Long populacaoMenorQue, Pageable pageable) {
        return paisRepository.findByFiltros(regiao, populacaoMaiorQue, populacaoMenorQue, pageable);
    }
}
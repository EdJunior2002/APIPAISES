package com.meuprojeto.apipaises.service;

import com.meuprojeto.apipaises.model.Pais;
import com.meuprojeto.apipaises.repository.PaisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PaisService {

    @Autowired
    private PaisRepository paisRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    // Método para importar países de uma API externa
    public void importarPaises() {
        String url = "https://restcountries.com/v3.1/all";
        Pais[] paisesExternos = restTemplate.getForObject(url, Pais[].class);
        if (paisesExternos != null) {
            paisRepository.saveAll(Arrays.asList(paisesExternos));
        }
    }

    // Método para criar um novo país
    public Pais criar(Pais pais) {
        return paisRepository.save(pais);
    }

    // Método para listar todos os países
    public List<Pais> listarTodos() {
        return paisRepository.findAll();
    }

    // Método para buscar país por ID
    public Optional<Pais> buscarPorId(Long id) {
        return paisRepository.findById(id);
    }

    // Método para listar países com filtros de região e população
    public Page<Pais> listarComFiltros(String regiao, Long populacaoMaiorQue, Long populacaoMenorQue, Pageable pageable) {
        return paisRepository.findByFiltros(regiao, populacaoMaiorQue, populacaoMenorQue, pageable);
    }

    // Método para atualizar um país
    public Optional<Pais> atualizar(Long id, Pais paisAtualizado) {
        return paisRepository.findById(id).map(paisExistente -> {
            paisExistente.setNome(paisAtualizado.getNome());
            paisExistente.setRegiao(paisAtualizado.getRegiao());
            paisExistente.setPopulacao(paisAtualizado.getPopulacao());
            paisExistente.setCapital(paisAtualizado.getCapital());
            return paisRepository.save(paisExistente);
        });
    }

    // Método para deletar um país
    public boolean deletar(Long id) {
        if (paisRepository.existsById(id)) {
            paisRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
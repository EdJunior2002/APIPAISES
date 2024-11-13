package com.meuprojeto.apipaises.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.meuprojeto.apipaises.model.Pais;
import com.meuprojeto.apipaises.repository.PaisRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;

@RestController
@RequestMapping("/paises/v1.0")
public class PaisController {

    @Autowired
    private PaisRepository paisRepository;

    // Endpoint para listar todos os países
    @GetMapping
    public List<Pais> listarTodos() {
        return paisRepository.findAll();
    }

    // Endpoint para buscar um país por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pais> buscarPorId(@PathVariable Long id) {
        Optional<Pais> pais = paisRepository.findById(id);
        return pais.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para importar a lista de países de uma API externa
    @PostMapping("/importar")
    public ResponseEntity<List<Pais>> importarPaises() {
        String apiUrl = "https://restcountries.com/v3.1/all";
        RestTemplate restTemplate = new RestTemplate();

        try {
            Pais[] paisesArray = restTemplate.getForObject(apiUrl, Pais[].class);
            if (paisesArray != null) {
                List<Pais> paisesImportados = Arrays.asList(paisesArray);
                paisRepository.saveAll(paisesImportados);
                return ResponseEntity.status(HttpStatus.CREATED).body(paisesImportados);
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint para adicionar um novo país manualmente
    @PostMapping
    public ResponseEntity<Pais> adicionarPais(@RequestBody Pais novoPais) {
        Pais paisSalvo = paisRepository.save(novoPais);
        return ResponseEntity.status(HttpStatus.CREATED).body(paisSalvo);
    }

    // Endpoint para atualizar um país existente
    @PutMapping("/{id}")
    public ResponseEntity<Pais> atualizarPais(@PathVariable Long id, @RequestBody Pais paisAtualizado) {
        if (!paisRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        paisAtualizado.setId(id);
        Pais paisSalvo = paisRepository.save(paisAtualizado);
        return ResponseEntity.ok(paisSalvo);
    }

    // Endpoint para deletar um país por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPais(@PathVariable Long id) {
        if (!paisRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        paisRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

package com.meuprojeto.apipaises.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.meuprojeto.apipaises.model.Pais;
import com.meuprojeto.apipaises.repository.PaisRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;

@RestController
@RequestMapping("/paises/v1.0")
@Tag(name = "Países", description = "Endpoints para gerenciar países")
public class PaisController {

    @Autowired
    private PaisRepository paisRepository;

    @Operation(summary = "Listar todos os países", description = "Retorna uma lista com todos os países armazenados.")
    @GetMapping("/todos")
    public List<Pais> listarTodos() {
        return paisRepository.findAll();
    }

    @Operation(summary = "Listar países com filtros, ordenação e paginação",
            description = "Permite listar países aplicando filtros por região e população, além de ordenação e paginação.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de países filtrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pais.class)))
            })
    @GetMapping
    public ResponseEntity<Page<Pais>> listarComFiltros(
            @RequestParam(required = false) String regiao,
            @RequestParam(required = false) Long populacaoMaiorQue,
            @RequestParam(required = false) Long populacaoMenorQue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {

        Sort sortConfig = Sort.by(sort.startsWith("-") ? Sort.Order.desc(sort.substring(1)) : Sort.Order.asc(sort));
        Pageable pageable = PageRequest.of(page, size, sortConfig);

        Page<Pais> resultados = paisRepository.findByFiltros(regiao, populacaoMaiorQue, populacaoMenorQue, pageable);

        return ResponseEntity.ok(resultados);
    }

    @Operation(summary = "Buscar país por ID",
            description = "Retorna os detalhes de um país com base no ID fornecido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "País encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pais.class))),
                    @ApiResponse(responseCode = "404", description = "País não encontrado")
            })
    @GetMapping("/{id}")
    public ResponseEntity<Pais> buscarPorId(@PathVariable Long id) {
        Optional<Pais> pais = paisRepository.findById(id);
        return pais.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Importar lista de países",
            description = "Importa uma lista de países de uma API externa e salva no banco de dados.")
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

    @Operation(summary = "Adicionar um novo país",
            description = "Adiciona um país manualmente ao banco de dados.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "País adicionado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida")
            })
    @PostMapping
    public ResponseEntity<Pais> adicionarPais(@RequestBody Pais novoPais) {
        Pais paisSalvo = paisRepository.save(novoPais);
        return ResponseEntity.status(HttpStatus.CREATED).body(paisSalvo);
    }

    @Operation(summary = "Atualizar dados de um país",
            description = "Atualiza os dados de um país existente com base no ID fornecido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "País atualizado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "País não encontrado")
            })
    @PutMapping("/{id}")
    public ResponseEntity<Pais> atualizarPais(@PathVariable Long id, @RequestBody Pais paisAtualizado) {
        if (!paisRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        paisAtualizado.setId(id);
        Pais paisSalvo = paisRepository.save(paisAtualizado);
        return ResponseEntity.ok(paisSalvo);
    }

    @Operation(summary = "Atualizar parcialmente um país",
            description = "Atualiza um ou mais atributos de um país existente com base no ID fornecido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "País atualizado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "País não encontrado"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida")
            })
    @PatchMapping("/{id}")
    public ResponseEntity<Pais> atualizarParcialmentePais(@PathVariable Long id, @RequestBody Map<String, Object> campos) {
        Optional<Pais> paisOptional = paisRepository.findById(id);

        if (!paisOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Pais pais = paisOptional.get();
        
        campos.forEach((chave, valor) -> {
            switch (chave) {
                case "nome":
                    if (valor instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, String> nomeMap = (Map<String, String>) valor;
                        if (nomeMap.containsKey("common")) {
                            pais.getNome().setCommon(nomeMap.get("common"));
                        }
                    }
                    break;
                case "capital":
                    if (valor instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<String> capitalList = (List<String>) valor;
                        pais.setCapital(capitalList);
                    }
                    break;
                case "regiao":
                    if (valor instanceof String) {
                        pais.setRegiao((String) valor);
                    }
                    break;
                case "populacao":
                    if (valor instanceof Number) {
                        pais.setPopulacao(((Number) valor).longValue());
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Campo inválido: " + chave);
            }
        });

        Pais paisAtualizado = paisRepository.save(pais);
        return ResponseEntity.ok(paisAtualizado);
    }

    @Operation(summary = "Deletar um país",
            description = "Remove um país do banco de dados com base no ID fornecido.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "País deletado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "País não encontrado")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPais(@PathVariable Long id) {
        if (!paisRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        paisRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

package com.meuprojeto.apipaises.controller;

import com.meuprojeto.apipaises.model.Pais;
import com.meuprojeto.apipaises.service.PaisService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/helloWorld/v1")
@Tag(name = "Países", description = "Endpoints para gerenciar países")
public class PaisController {

    @Autowired
    private PaisService paisService;

    // Endpoint para listar todos os países
    @Operation(summary = "Listar todos os países", description = "Retorna uma lista com todos os países armazenados.")
    @GetMapping("/todos")
    public List<Pais> listarTodos() {
        return paisService.listarTodos();
    }

    // Endpoint para listar países com filtros, ordenação e paginação
    @Operation(summary = "Listar países com filtros, ordenação e paginação",
            description = "Permite listar países aplicando filtros por região e população, além de ordenação e paginação.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de países filtrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pais.class)))
            })
    @GetMapping("/filtro")
    public ResponseEntity<Page<Pais>> listarComFiltros(
            @RequestParam(required = false) String regiao,
            @RequestParam(required = false) Long populacaoMaiorQue,
            @RequestParam(required = false) Long populacaoMenorQue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {

        Sort sortConfig = Sort.by(sort.startsWith("-") ? Sort.Order.desc(sort.substring(1)) : Sort.Order.asc(sort));
        Pageable pageable = PageRequest.of(page, size, sortConfig);

        Page<Pais> resultados = paisService.listarComFiltros(regiao, populacaoMaiorQue, populacaoMenorQue, pageable);

        return ResponseEntity.ok(resultados);
    }

    // Endpoint para buscar país por ID
    @Operation(summary = "Buscar país por ID", description = "Retorna os detalhes de um país com base no ID fornecido.")
    @GetMapping("/{id}")
    public ResponseEntity<Pais> buscarPorId(@PathVariable Long id) {
        Optional<Pais> pais = paisService.buscarPorId(id);
        return pais.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para criar um novo país
    @Operation(summary = "Criar um novo país", description = "Cria um novo país e armazena no banco de dados.")
    @PostMapping("/criar")
    public ResponseEntity<Pais> criarPais(@RequestBody Pais pais) {
        Pais novoPais = paisService.criar(pais);
        return ResponseEntity.ok(novoPais);
    }

    // Endpoint para atualizar um país
    @Operation(summary = "Atualizar um país", description = "Atualiza os detalhes de um país existente com base no ID fornecido.")
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Pais> atualizarPais(@PathVariable Long id, @RequestBody Pais pais) {
        Optional<Pais> paisAtualizado = paisService.atualizar(id, pais);
        return paisAtualizado.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para deletar um país
    @Operation(summary = "Deletar um país", description = "Remove um país com base no ID fornecido.")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarPais(@PathVariable Long id) {
        boolean deletado = paisService.deletar(id);
        if (deletado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para importar países de uma API externa
    @Operation(summary = "Importar países", description = "Importa países de uma API externa e armazena no banco de dados.")
    @PostMapping("/importar")
    public ResponseEntity<String> importarPaises() {
        paisService.importarPaises();
        return ResponseEntity.ok("Países importados com sucesso!");
    }
}
package com.petshop.api.controller;

import com.petshop.api.dto.CategoriaDTO;
import com.petshop.api.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Operacoes de cadastro de categorias de produtos")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista todas as categorias")
    public List<CategoriaDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca categoria por ID")
    public CategoriaDTO buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cria uma nova categoria")
    public ResponseEntity<CategoriaDTO> criar(@Valid @RequestBody CategoriaDTO dto) {
        CategoriaDTO criada = service.criar(dto);
        return ResponseEntity.created(URI.create("/api/categorias/" + criada.getId())).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza categoria existente")
    public CategoriaDTO atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove categoria")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}

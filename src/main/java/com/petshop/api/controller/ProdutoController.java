package com.petshop.api.controller;

import com.petshop.api.dto.ProdutoDTO;
import com.petshop.api.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "Operacoes de cadastro de produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista produtos com filtros opcionais")
    public List<ProdutoDTO> listar(
            @Parameter(description = "Filtrar por categoria") @RequestParam(required = false) Long categoriaId,
            @Parameter(description = "Buscar por nome") @RequestParam(required = false) String nome,
            @Parameter(description = "Somente ativos") @RequestParam(required = false) Boolean ativo) {
        return service.listar(categoriaId, nome, ativo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca produto por ID")
    public ProdutoDTO buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cria novo produto")
    public ResponseEntity<ProdutoDTO> criar(@Valid @RequestBody ProdutoDTO dto) {
        ProdutoDTO criado = service.criar(dto);
        return ResponseEntity.created(URI.create("/api/produtos/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza produto existente")
    public ProdutoDTO atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove produto")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}

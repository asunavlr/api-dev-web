package com.petshop.api.service;

import com.petshop.api.dto.ProdutoDTO;
import com.petshop.api.entity.Categoria;
import com.petshop.api.entity.Produto;
import com.petshop.api.exception.ResourceNotFoundException;
import com.petshop.api.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProdutoService {

    private final ProdutoRepository repository;
    private final CategoriaService categoriaService;

    public ProdutoService(ProdutoRepository repository, CategoriaService categoriaService) {
        this.repository = repository;
        this.categoriaService = categoriaService;
    }

    public List<ProdutoDTO> listar(Long categoriaId, String nome, Boolean ativo) {
        List<Produto> produtos;
        if (categoriaId != null) {
            produtos = repository.findByCategoriaId(categoriaId);
        } else if (nome != null && !nome.isBlank()) {
            produtos = repository.findByNomeContainingIgnoreCase(nome);
        } else if (Boolean.TRUE.equals(ativo)) {
            produtos = repository.findByAtivoTrue();
        } else {
            produtos = repository.findAll();
        }
        return produtos.stream().map(this::toDTO).toList();
    }

    public ProdutoDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    @Transactional
    public ProdutoDTO criar(ProdutoDTO dto) {
        Categoria categoria = categoriaService.buscarEntidade(dto.getCategoriaId());
        Produto p = new Produto();
        p.setNome(dto.getNome());
        p.setDescricao(dto.getDescricao());
        p.setPreco(dto.getPreco());
        p.setEstoque(dto.getEstoque());
        p.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        p.setCategoria(categoria);
        return toDTO(repository.save(p));
    }

    @Transactional
    public ProdutoDTO atualizar(Long id, ProdutoDTO dto) {
        Produto p = buscarEntidade(id);
        Categoria categoria = categoriaService.buscarEntidade(dto.getCategoriaId());
        p.setNome(dto.getNome());
        p.setDescricao(dto.getDescricao());
        p.setPreco(dto.getPreco());
        p.setEstoque(dto.getEstoque());
        p.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : p.getAtivo());
        p.setCategoria(categoria);
        return toDTO(repository.save(p));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Produto", id);
        }
        repository.deleteById(id);
    }

    public Produto buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto", id));
    }

    @Transactional
    public Produto salvar(Produto p) {
        return repository.save(p);
    }

    private ProdutoDTO toDTO(Produto p) {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(p.getId());
        dto.setNome(p.getNome());
        dto.setDescricao(p.getDescricao());
        dto.setPreco(p.getPreco());
        dto.setEstoque(p.getEstoque());
        dto.setAtivo(p.getAtivo());
        if (p.getCategoria() != null) {
            dto.setCategoriaId(p.getCategoria().getId());
            dto.setCategoriaNome(p.getCategoria().getNome());
        }
        return dto;
    }
}

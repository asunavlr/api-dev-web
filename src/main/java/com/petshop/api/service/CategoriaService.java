package com.petshop.api.service;

import com.petshop.api.dto.CategoriaDTO;
import com.petshop.api.entity.Categoria;
import com.petshop.api.exception.BusinessException;
import com.petshop.api.exception.ResourceNotFoundException;
import com.petshop.api.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public List<CategoriaDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public CategoriaDTO buscarPorId(Long id) {
        Categoria c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", id));
        return toDTO(c);
    }

    @Transactional
    public CategoriaDTO criar(CategoriaDTO dto) {
        if (repository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new BusinessException("Ja existe uma categoria com o nome '" + dto.getNome() + "'");
        }
        Categoria c = new Categoria(dto.getNome(), dto.getDescricao());
        return toDTO(repository.save(c));
    }

    @Transactional
    public CategoriaDTO atualizar(Long id, CategoriaDTO dto) {
        Categoria c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", id));
        if (!c.getNome().equalsIgnoreCase(dto.getNome())
                && repository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new BusinessException("Ja existe uma categoria com o nome '" + dto.getNome() + "'");
        }
        c.setNome(dto.getNome());
        c.setDescricao(dto.getDescricao());
        return toDTO(repository.save(c));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria", id);
        }
        repository.deleteById(id);
    }

    public Categoria buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", id));
    }

    private CategoriaDTO toDTO(Categoria c) {
        return new CategoriaDTO(c.getId(), c.getNome(), c.getDescricao());
    }
}

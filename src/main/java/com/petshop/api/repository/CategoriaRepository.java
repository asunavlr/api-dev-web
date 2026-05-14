package com.petshop.api.repository;

import com.petshop.api.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCase(String nome);
}

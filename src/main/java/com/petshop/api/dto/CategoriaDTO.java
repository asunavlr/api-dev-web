package com.petshop.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoriaDTO {

    private Long id;

    @NotBlank(message = "nome é obrigatório")
    @Size(max = 80, message = "nome deve ter no maximo 80 caracteres")
    private String nome;

    @Size(max = 255, message = "descricao deve ter no maximo 255 caracteres")
    private String descricao;

    public CategoriaDTO() {}

    public CategoriaDTO(Long id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}

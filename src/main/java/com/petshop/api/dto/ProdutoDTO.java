package com.petshop.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ProdutoDTO {

    private Long id;

    @NotBlank(message = "nome é obrigatório")
    @Size(max = 120)
    private String nome;

    @Size(max = 500)
    private String descricao;

    @NotNull(message = "preco é obrigatório")
    @DecimalMin(value = "0.01", message = "preco deve ser maior que zero")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal preco;

    @NotNull(message = "estoque é obrigatório")
    @Min(value = 0, message = "estoque nao pode ser negativo")
    private Integer estoque;

    private Boolean ativo = true;

    @NotNull(message = "categoriaId é obrigatório")
    private Long categoriaId;

    private String categoriaNome;

    public ProdutoDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public String getCategoriaNome() { return categoriaNome; }
    public void setCategoriaNome(String categoriaNome) { this.categoriaNome = categoriaNome; }
}

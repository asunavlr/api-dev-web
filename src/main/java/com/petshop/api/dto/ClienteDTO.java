package com.petshop.api.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class ClienteDTO {

    private Long id;

    @NotBlank(message = "nome é obrigatório")
    @Size(max = 120)
    private String nome;

    @NotBlank(message = "email é obrigatório")
    @Email(message = "email invalido")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "cpf é obrigatório")
    @Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "cpf deve ter 11 digitos ou formato 000.000.000-00")
    private String cpf;

    @Size(max = 20)
    private String telefone;

    @Size(max = 255)
    private String endereco;

    private LocalDateTime criadoEm;

    public ClienteDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}

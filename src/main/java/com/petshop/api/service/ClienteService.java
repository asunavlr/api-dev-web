package com.petshop.api.service;

import com.petshop.api.dto.ClienteDTO;
import com.petshop.api.entity.Cliente;
import com.petshop.api.exception.BusinessException;
import com.petshop.api.exception.ResourceNotFoundException;
import com.petshop.api.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public List<ClienteDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public ClienteDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    @Transactional
    public ClienteDTO criar(ClienteDTO dto) {
        String cpfLimpo = limparCpf(dto.getCpf());
        if (repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email ja cadastrado");
        }
        if (repository.existsByCpf(cpfLimpo)) {
            throw new BusinessException("CPF ja cadastrado");
        }
        Cliente c = new Cliente();
        c.setNome(dto.getNome());
        c.setEmail(dto.getEmail());
        c.setCpf(cpfLimpo);
        c.setTelefone(dto.getTelefone());
        c.setEndereco(dto.getEndereco());
        return toDTO(repository.save(c));
    }

    @Transactional
    public ClienteDTO atualizar(Long id, ClienteDTO dto) {
        Cliente c = buscarEntidade(id);
        String cpfLimpo = limparCpf(dto.getCpf());
        if (!c.getEmail().equalsIgnoreCase(dto.getEmail()) && repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email ja cadastrado");
        }
        if (!c.getCpf().equals(cpfLimpo) && repository.existsByCpf(cpfLimpo)) {
            throw new BusinessException("CPF ja cadastrado");
        }
        c.setNome(dto.getNome());
        c.setEmail(dto.getEmail());
        c.setCpf(cpfLimpo);
        c.setTelefone(dto.getTelefone());
        c.setEndereco(dto.getEndereco());
        return toDTO(repository.save(c));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente", id);
        }
        repository.deleteById(id);
    }

    public Cliente buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }

    private String limparCpf(String cpf) {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }

    private ClienteDTO toDTO(Cliente c) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(c.getId());
        dto.setNome(c.getNome());
        dto.setEmail(c.getEmail());
        dto.setCpf(c.getCpf());
        dto.setTelefone(c.getTelefone());
        dto.setEndereco(c.getEndereco());
        dto.setCriadoEm(c.getCriadoEm());
        return dto;
    }
}

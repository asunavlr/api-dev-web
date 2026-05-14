package com.petshop.api.controller;

import com.petshop.api.dto.PedidoDTO;
import com.petshop.api.dto.PedidoRequestDTO;
import com.petshop.api.dto.StatusUpdateDTO;
import com.petshop.api.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Controle de pedidos e itens")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista pedidos, opcionalmente filtrados por cliente")
    public List<PedidoDTO> listar(@RequestParam(required = false) Long clienteId) {
        return service.listar(clienteId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca pedido por ID")
    public PedidoDTO buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cria novo pedido com itens e baixa estoque")
    public ResponseEntity<PedidoDTO> criar(@Valid @RequestBody PedidoRequestDTO req) {
        PedidoDTO criado = service.criar(req);
        return ResponseEntity.created(URI.create("/api/pedidos/" + criado.getId())).body(criado);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualiza status do pedido")
    public PedidoDTO atualizarStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateDTO body) {
        return service.atualizarStatus(id, body.getStatus());
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancela o pedido e devolve estoque")
    public PedidoDTO cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove pedido (apenas se PENDENTE ou CANCELADO)")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}

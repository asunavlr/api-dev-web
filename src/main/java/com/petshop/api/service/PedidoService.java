package com.petshop.api.service;

import com.petshop.api.dto.ItemPedidoDTO;
import com.petshop.api.dto.PedidoDTO;
import com.petshop.api.dto.PedidoRequestDTO;
import com.petshop.api.entity.*;
import com.petshop.api.exception.BusinessException;
import com.petshop.api.exception.ResourceNotFoundException;
import com.petshop.api.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteService clienteService,
                         ProdutoService produtoService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
    }

    public List<PedidoDTO> listar(Long clienteId) {
        List<Pedido> pedidos = (clienteId != null)
                ? pedidoRepository.findByClienteId(clienteId)
                : pedidoRepository.findAll();
        return pedidos.stream().map(this::toDTO).toList();
    }

    public PedidoDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    @Transactional
    public PedidoDTO criar(PedidoRequestDTO req) {
        Cliente cliente = clienteService.buscarEntidade(req.getClienteId());

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.PENDENTE);

        for (ItemPedidoDTO itemDTO : req.getItens()) {
            Produto produto = produtoService.buscarEntidade(itemDTO.getProdutoId());

            if (Boolean.FALSE.equals(produto.getAtivo())) {
                throw new BusinessException("Produto '" + produto.getNome() + "' esta inativo");
            }
            if (produto.getEstoque() < itemDTO.getQuantidade()) {
                throw new BusinessException("Estoque insuficiente para '" + produto.getNome()
                        + "'. Disponivel: " + produto.getEstoque());
            }
            produto.setEstoque(produto.getEstoque() - itemDTO.getQuantidade());
            produtoService.salvar(produto);

            ItemPedido item = new ItemPedido(produto, itemDTO.getQuantidade());
            pedido.addItem(item);
        }

        pedido.recalcularTotal();
        return toDTO(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoDTO atualizarStatus(Long id, StatusPedido novoStatus) {
        Pedido pedido = buscarEntidade(id);
        if (pedido.getStatus() == StatusPedido.CANCELADO || pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new BusinessException("Pedido em status " + pedido.getStatus() + " nao pode ser alterado");
        }
        pedido.setStatus(novoStatus);
        return toDTO(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoDTO cancelar(Long id) {
        Pedido pedido = buscarEntidade(id);
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new BusinessException("Pedido ja esta cancelado");
        }
        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new BusinessException("Pedido entregue nao pode ser cancelado");
        }
        // devolve estoque
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoque(produto.getEstoque() + item.getQuantidade());
            produtoService.salvar(produto);
        }
        pedido.setStatus(StatusPedido.CANCELADO);
        return toDTO(pedidoRepository.save(pedido));
    }

    @Transactional
    public void deletar(Long id) {
        Pedido pedido = buscarEntidade(id);
        if (pedido.getStatus() != StatusPedido.CANCELADO && pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new BusinessException("Apenas pedidos PENDENTE ou CANCELADO podem ser removidos");
        }
        pedidoRepository.delete(pedido);
    }

    public Pedido buscarEntidade(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
    }

    private PedidoDTO toDTO(Pedido p) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(p.getId());
        dto.setClienteId(p.getCliente().getId());
        dto.setClienteNome(p.getCliente().getNome());
        dto.setDataPedido(p.getDataPedido());
        dto.setStatus(p.getStatus());
        dto.setTotal(p.getTotal());
        dto.setItens(p.getItens().stream().map(this::itemToDTO).toList());
        return dto;
    }

    private ItemPedidoDTO itemToDTO(ItemPedido i) {
        ItemPedidoDTO dto = new ItemPedidoDTO();
        dto.setId(i.getId());
        dto.setProdutoId(i.getProduto().getId());
        dto.setProdutoNome(i.getProduto().getNome());
        dto.setQuantidade(i.getQuantidade());
        dto.setPrecoUnitario(i.getPrecoUnitario());
        dto.setSubtotal(i.getSubtotal());
        return dto;
    }
}

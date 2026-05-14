package com.petshop.api.dto;

import com.petshop.api.entity.StatusPedido;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateDTO {

    @NotNull(message = "status é obrigatório")
    private StatusPedido status;

    public StatusUpdateDTO() {}

    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
}

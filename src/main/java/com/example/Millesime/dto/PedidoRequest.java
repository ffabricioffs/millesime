package com.example.Millesime.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class PedidoRequest {

    @NotEmpty(message = "Carrinho não pode estar vazio")
    @Valid
    private List<ItemPedidoRequest> itens;

    public List<ItemPedidoRequest> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoRequest> itens) {
        this.itens = itens;
    }
}

package com.example.Millesime.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class Pedido {

    private UUID id;
    private UUID clienteId;
    private LocalDateTime dataPedido;
    private Double total;
    private String status;
    private List<ItemPedido> itens;

    public String getDataFormatada() {
        if (dataPedido == null) return "";
        return dataPedido.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public Pedido() {
        this.dataPedido = LocalDateTime.now();
        this.status = "PENDENTE";
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public void setClienteId(UUID clienteId) {
        this.clienteId = clienteId;
    }

    public LocalDateTime getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDateTime dataPedido) {
        this.dataPedido = dataPedido;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }
}

package com.example.Millesime.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Avaliacao {

    private UUID id;
    private UUID produtoId;
    private UUID clienteId;
    private String clienteNome;
    private int nota;
    private String comentario;
    private LocalDateTime data;

    public Avaliacao() {
        this.id = UUID.randomUUID();
        this.data = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProdutoId() { return produtoId; }
    public void setProdutoId(UUID produtoId) { this.produtoId = produtoId; }
    public UUID getClienteId() { return clienteId; }
    public void setClienteId(UUID clienteId) { this.clienteId = clienteId; }
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
}

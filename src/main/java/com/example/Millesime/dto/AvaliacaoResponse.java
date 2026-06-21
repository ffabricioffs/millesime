package com.example.Millesime.dto;

import java.util.UUID;

public class AvaliacaoResponse {

    private UUID id;
    private String clienteNome;
    private int nota;
    private String comentario;
    private String dataFormatada;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public String getDataFormatada() { return dataFormatada; }
    public void setDataFormatada(String dataFormatada) { this.dataFormatada = dataFormatada; }
}

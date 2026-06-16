package com.example.Millesime.dto;

import java.util.UUID;

public class ClienteSession {

    private UUID id;
    private String nomeCompleto;
    private String email;
    private String cpf;
    private String telefone;
    private boolean newsletter;
    private String dataCadastroFormatada;

    public ClienteSession() {
    }

    public ClienteSession(UUID id, String nomeCompleto, String email,
                          String cpf, String telefone, boolean newsletter,
                          String dataCadastroFormatada) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.cpf = cpf;
        this.telefone = telefone;
        this.newsletter = newsletter;
        this.dataCadastroFormatada = dataCadastroFormatada;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean isNewsletter() {
        return newsletter;
    }

    public void setNewsletter(boolean newsletter) {
        this.newsletter = newsletter;
    }

    public String getDataCadastroFormatada() {
        return dataCadastroFormatada;
    }

    public void setDataCadastroFormatada(String dataCadastroFormatada) {
        this.dataCadastroFormatada = dataCadastroFormatada;
    }
}

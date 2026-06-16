package com.example.Millesime.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Produto {

    private UUID id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String descricao;

    @NotBlank(message = "Tipo é obrigatório")
    private String tipo;

    private String regiao;
    private String pais;
    private String uva;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Pre\u00e7o deve ser maior que zero")
    private Double preco;

    @NotNull
    @Min(value = 0, message = "Estoque não pode ser negativo")
    private Integer estoque;
    private String imagem;
    private LocalDateTime dataCriacao;
    private boolean ativo;

    public Produto() {
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
    }

    public Produto(UUID id, String nome, String descricao, String tipo, String regiao, String pais,
                   String uva, Double preco, Integer estoque, String imagem, LocalDateTime dataCriacao, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
        this.regiao = regiao;
        this.pais = pais;
        this.uva = uva;
        this.preco = preco;
        this.estoque = estoque;
        this.imagem = imagem;
        this.dataCriacao = dataCriacao;
        this.ativo = ativo;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getUva() {
        return uva;
    }

    public void setUva(String uva) {
        this.uva = uva;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", tipo='" + tipo + '\'' +
                ", preco=" + preco +
                ", estoque=" + estoque +
                ", ativo=" + ativo +
                '}';
    }
}

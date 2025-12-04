package com.firebase.remedios;

import java.util.Date;

public class Recompensa {

    private String nome;
    private String imagem;
    private Date data;

    public Recompensa() {}

    public Recompensa(String nome, String imagem, Date data) {
        this.nome = nome;
        this.imagem = imagem;
        this.data = data;
    }

    public String getNome() { return nome; }
    public String getImagem() { return imagem; }
    public Date getData() { return data; }

    public void setNome(String nome) { this.nome = nome; }
    public void setImagem(String imagem) { this.imagem = imagem; }
    public void setData(Date data) { this.data = data; }
}


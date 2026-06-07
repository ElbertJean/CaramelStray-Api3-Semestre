package br.com.AllTallent.dto;

import br.com.AllTallent.model.Perfil;

@SuppressWarnings("java:S120")
public record PerfilDTO(Integer codigo, String nome, String descricao) {
    public PerfilDTO(Perfil perfil) {
        this(perfil.getCodigo(), perfil.getNome(), perfil.getDescricao());
    }
}

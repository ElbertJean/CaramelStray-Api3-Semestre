package br.com.AllTallent.dto;

import br.com.AllTallent.model.Perfil;

public record PerfilDTO(Integer codigo, String nome, String descricao) {
    public PerfilDTO(Perfil perfil) {
        this(perfil.getCodigo(), perfil.getNome(), perfil.getDescricao());
    }
}

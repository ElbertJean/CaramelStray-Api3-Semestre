package br.com.alltallent.dto;

import br.com.alltallent.model.Area;

@SuppressWarnings("java:S120")
public record AreaDTO(Integer codigo, String nome, String descricao) {
    public AreaDTO(Area area) {
        this(area.getCodigo(), area.getNome(), area.getDescricao());
    }
}

package br.com.toquerendo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoriaUsuarioEnum {

    TURISTA(1L, "Turista"),
    VENDEDOR(2L, "Vendedor");

    private Long id;
    private String descricao;

}

package br.com.toquerendo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum CategoriaUsuarioEnum {

    TURISTA(1, "Turista", List.of("ROLE_TURISTA")),
    VENDEDOR(2, "Vendedor", List.of("ROLE_TURISTA", "ROLE_VENDEDOR")),
    ADMINISTRADOR(3, "Administrador", List.of("ROLE_ADMIN"));

    private Integer id;
    private String descricao;
    private List<String> roles;

    public static CategoriaUsuarioEnum fromId(Integer id) {
        for (CategoriaUsuarioEnum categoria : values()) {
            if (categoria.getId().equals(id)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("CategoriaUsuarioEnum inválida: " + id);
    }

}

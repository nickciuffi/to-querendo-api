package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.Categoria;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoriaOutputDto {

    private Integer id;
    private String descricao;

    public static CategoriaOutputDto fromEntity(Categoria categoria) {
        if(categoria == null) {
            return null;
        }
        CategoriaOutputDto dto = new CategoriaOutputDto();
        dto.setId(categoria.getId());
        dto.setDescricao(categoria.getDescricao());
        return dto;
    }
}

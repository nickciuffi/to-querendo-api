package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.ProdutoBase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProdutoBaseOutputDto {

    @Schema(description = "Identificador do produto base", example = "1")
    private Long id;

    @Schema(description = "Nome do produto base", example = "Água de coco")
    private String nome;

    @Schema(description = "Descrição do produto base", example = "Água de coco gelada, servida no próprio coco")
    private String descricao;

    @Schema(description = "URL da foto do produto", example = "https://exemplo.com/agua-de-coco.jpg")
    private String urlFoto;

    @Schema(description = "Preço mínimo sugerido para o produto", example = "5.00")
    private String precoMinimo;

    @Schema(description = "Indica se o produto está ativo e disponível para venda", example = "true")
    private Boolean estaAtivo;

    public static ProdutoBaseOutputDto fromEntity(ProdutoBase produtoBase) {
        return ProdutoBaseOutputDto.builder()
                .id(produtoBase.getId())
                .nome(produtoBase.getNome())
                .descricao(produtoBase.getDescricao())
                .urlFoto(produtoBase.getUrlFoto())
                .precoMinimo(produtoBase.getPrecoMinimo().toString())
                .estaAtivo(produtoBase.getEstaAtivo())
                .build();
    }
}

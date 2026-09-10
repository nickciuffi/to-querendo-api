package br.com.toquerendo.dto.input;

import br.com.toquerendo.entity.ProdutoBase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CriarProdutoBaseInputDto {

    @Schema(description = "Nome do produto base", example = "Água de coco", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String nome;

    @Schema(description = "Descrição do produto base", example = "Água de coco gelada, servida no próprio coco", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String descricao;

    @Schema(description = "URL da foto do produto", example = "https://exemplo.com/agua-de-coco.jpg")
    private String urlFoto;

    @Schema(description = "Preço mínimo sugerido para o produto", example = "5.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private BigDecimal precoMinimo;

    public static ProdutoBase toEntity(CriarProdutoBaseInputDto dto) {
        ProdutoBase produtoBase = new ProdutoBase();
        produtoBase.setNome(dto.getNome());
        produtoBase.setDescricao(dto.getDescricao());
        produtoBase.setUrlFoto(dto.getUrlFoto());
        produtoBase.setPrecoMinimo(dto.getPrecoMinimo());
        produtoBase.setEstaAtivo(Boolean.TRUE);
        return produtoBase;
    }
}

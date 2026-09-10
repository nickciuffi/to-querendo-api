package br.com.toquerendo.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CriarProdutoEspecificoInputDto {

    @Schema(description = "Nome do produto específico", example = "Água de coco gelada da praia", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String nome;

    @Schema(description = "Descrição do produto específico", example = "Água de coco geladinha, servida no próprio coco")
    private String descricao;

    @Schema(description = "URL da foto do produto", example = "https://exemplo.com/agua-de-coco.jpg")
    private String urlFoto;

    @Schema(description = "Identificador do produto base ao qual este produto está associado", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long idProdutoBase;

    @Schema(description = "Preço de venda do produto", example = "6.50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private BigDecimal preco;
}

package br.com.toquerendo.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CriarIntencaoCompraInputDto {

    @Schema(description = "Identificador do produto base ao qual a intenção de compra está associada", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long idProdutoBase;

    @Schema(description = "Descrição do local onde o turista deseja receber o produto", example = "Guarda-sol azul, próximo ao quiosque 3")
    @NotNull
    private String descricaoLocal;

    @Schema(description = "Observações adicionais sobre a intenção de compra", example = "Sem açúcar, por favor")
    private String observacoes;

    @Schema(description = "URL de uma foto do local para ajudar o vendedor a localizar o turista", example = "https://exemplo.com/local.jpg")
    private String urlFotoLocal;
}

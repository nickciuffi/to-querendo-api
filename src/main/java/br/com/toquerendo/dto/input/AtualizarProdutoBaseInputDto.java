package br.com.toquerendo.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AtualizarProdutoBaseInputDto {

    @Schema(description = "Nome do produto base", example = "Água de coco")
    private String nome;

    @Schema(description = "Descrição do produto base", example = "Água de coco gelada, servida no próprio coco")
    private String descricao;

    @Schema(description = "URL da foto do produto", example = "https://exemplo.com/agua-de-coco.jpg")
    private String urlFoto;

    @Schema(description = "Preço mínimo sugerido para o produto", example = "5.00")
    private BigDecimal precoMinimo;

    @Schema(description = "Indica se o produto está ativo e disponível para venda", example = "true")
    private Boolean estaAtivo;
}

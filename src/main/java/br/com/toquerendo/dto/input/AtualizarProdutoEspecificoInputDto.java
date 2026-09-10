package br.com.toquerendo.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AtualizarProdutoEspecificoInputDto {

    @Schema(description = "Nome do produto específico", example = "Água de coco gelada da praia")
    private String nome;

    @Schema(description = "Descrição do produto específico", example = "Água de coco geladinha, servida no próprio coco")
    private String descricao;

    @Schema(description = "URL da foto do produto", example = "https://exemplo.com/agua-de-coco.jpg")
    private String urlFoto;

    @Schema(description = "Preço de venda do produto", example = "6.50")
    private BigDecimal preco;

    @Schema(description = "Indica se o produto está ativo e disponível para venda", example = "true")
    private Boolean produtoAtivo;
}

package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.ProdutoEspecifico;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProdutoEspecificoOutputDto {

    @Schema(description = "Identificador do produto específico", example = "1")
    private Long id;

    @Schema(description = "Nome do produto específico", example = "Água de coco gelada da praia")
    private String nome;

    @Schema(description = "Descrição do produto específico", example = "Água de coco geladinha, servida no próprio coco")
    private String descricao;

    @Schema(description = "URL da foto do produto", example = "https://exemplo.com/agua-de-coco.jpg")
    private String urlFoto;

    @Schema(description = "Identificador do produto base associado", example = "1")
    private Long idProdutoBase;

    @Schema(description = "Nome do produto base associado", example = "Água de coco")
    private String nomeProdutoBase;

    @Schema(description = "Identificador do vendedor dono do produto", example = "1")
    private Long idVendedor;

    @Schema(description = "Preço de venda do produto", example = "6.50")
    private BigDecimal preco;

    @Schema(description = "Indica se o produto está ativo e disponível para venda", example = "true")
    private Boolean produtoAtivo;

    @Schema(description = "Data e hora de criação do produto")
    private LocalDateTime tsCriacaoProduto;

    public static ProdutoEspecificoOutputDto fromEntity(ProdutoEspecifico produtoEspecifico) {
        return ProdutoEspecificoOutputDto.builder()
                .id(produtoEspecifico.getId())
                .nome(produtoEspecifico.getNome())
                .descricao(produtoEspecifico.getDescricao())
                .urlFoto(produtoEspecifico.getUrlFoto())
                .idProdutoBase(produtoEspecifico.getProdutoBase().getId())
                .nomeProdutoBase(produtoEspecifico.getProdutoBase().getNome())
                .idVendedor(produtoEspecifico.getVendedor().getId())
                .preco(produtoEspecifico.getPreco())
                .produtoAtivo(produtoEspecifico.getProdutoAtivo())
                .tsCriacaoProduto(produtoEspecifico.getTsCriacaoProduto())
                .build();
    }
}

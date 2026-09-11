package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.IntencaoCompra;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntencaoCompraOutputDto {

    @Schema(description = "Identificador da intenção de compra", example = "1")
    private Long id;

    @Schema(description = "Identificador do usuário turista que criou a intenção de compra", example = "1")
    private Long idUsuario;

    @Schema(description = "Identificador do produto base associado", example = "1")
    private Long idProdutoBase;

    @Schema(description = "Nome do produto base associado", example = "Água de coco")
    private String nomeProdutoBase;

    @Schema(description = "Descrição do local onde o turista deseja receber o produto", example = "Guarda-sol azul, próximo ao quiosque 3")
    private String descricaoLocal;

    @Schema(description = "Observações adicionais sobre a intenção de compra", example = "Sem açúcar, por favor")
    private String observacoes;

    @Schema(description = "URL de uma foto do local para ajudar o vendedor a localizar o turista", example = "https://exemplo.com/local.jpg")
    private String urlFotoLocal;

    @Schema(description = "Data e hora de criação da intenção de compra")
    private LocalDateTime tsCriacaoIntencao;

    @Schema(description = "Data e hora de conclusão da intenção de compra")
    private LocalDateTime tsConclusaoIntencao;

    @Schema(description = "Indica se a intenção de compra está ativa", example = "true")
    private Boolean estaAtivo;

    public static IntencaoCompraOutputDto fromEntity(IntencaoCompra intencaoCompra) {
        return IntencaoCompraOutputDto.builder()
                .id(intencaoCompra.getId())
                .idUsuario(intencaoCompra.getUsuario().getId())
                .idProdutoBase(intencaoCompra.getProdutoBase().getId())
                .nomeProdutoBase(intencaoCompra.getProdutoBase().getNome())
                .descricaoLocal(intencaoCompra.getDescricaoLocal())
                .observacoes(intencaoCompra.getObservacoes())
                .urlFotoLocal(intencaoCompra.getUrlFotoLocal())
                .tsCriacaoIntencao(intencaoCompra.getTsCriacaoIntencao())
                .tsConclusaoIntencao(intencaoCompra.getTsConclusaoIntencao())
                .estaAtivo(intencaoCompra.getEstaAtivo())
                .build();
    }
}

package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.ProdutoBase;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProdutoBaseOutputDto {

    private Long id;
    private String nome;
    private String descricao;
    private String urlFoto;
    private String precoMinimo;
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

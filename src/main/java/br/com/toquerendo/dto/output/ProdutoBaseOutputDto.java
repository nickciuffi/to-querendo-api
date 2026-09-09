package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.ProdutoBase;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProdutoBaseOutputDto {
    private String nome;
    private String descricao;
    private String urlFoto;
    private String precoMinimo;

    public static ProdutoBaseOutputDto fromEntity(ProdutoBase produtoBase) {
        return ProdutoBaseOutputDto.builder()
                .nome(produtoBase.getNome())
                .descricao(produtoBase.getDescricao())
                .urlFoto(produtoBase.getUrlFoto())
                .precoMinimo(produtoBase.getPrecoMinimo().toString())
                .build();
    }
}

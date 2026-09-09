package br.com.toquerendo.dto.input;

import br.com.toquerendo.entity.ProdutoBase;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CriarProdutoBaseInputDto {

    @NotNull
    private String nome;

    @NotNull
    private String descricao;

    private String urlFoto;

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

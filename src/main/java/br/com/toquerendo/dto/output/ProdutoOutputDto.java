package br.com.toquerendo.dto.output;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProdutoOutputDto {
    private String nomeProduto;
    private String preco;
}

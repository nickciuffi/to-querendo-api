package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.Localizacao;
import br.com.toquerendo.entity.ProdutoEspecifico;
import br.com.toquerendo.entity.Vendedor;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendedorLocalizacaoProdutosOutputDto {

    @Schema(description = "Identificador do vendedor", example = "1")
    private Long idVendedor;

    @Schema(description = "Nome do vendedor", example = "João da Praia")
    private String nomeVendedor;

    private List<ProdutoEspecificoOutputDto> produtos;

    @Schema(description = "Latitude atual do vendedor", example = "-23.5614750")
    private BigDecimal latitude;

    @Schema(description = "Longitude atual do vendedor", example = "-46.6558830")
    private BigDecimal longitude;

    public static VendedorLocalizacaoProdutosOutputDto fromEntities(Vendedor vendedor, Localizacao localizacao, List<ProdutoEspecifico> produtos) {
        return VendedorLocalizacaoProdutosOutputDto.builder()
                .idVendedor(vendedor.getId())
                .nomeVendedor(vendedor.getUsuario().getNome())
                .latitude(localizacao != null ? localizacao.getLatitude() : new BigDecimal("0"))
                .longitude(localizacao != null ? localizacao.getLongitude() : new BigDecimal("0"))
                .produtos(produtos.stream().map(ProdutoEspecificoOutputDto::fromEntity).collect(Collectors.toList()))
                .build();
    }
}

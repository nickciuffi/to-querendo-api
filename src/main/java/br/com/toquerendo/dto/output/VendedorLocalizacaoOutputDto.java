package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.Localizacao;
import br.com.toquerendo.entity.ProdutoEspecifico;
import br.com.toquerendo.entity.Vendedor;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendedorLocalizacaoOutputDto {

    @Schema(description = "Identificador do vendedor", example = "1")
    private Long idVendedor;

    @Schema(description = "Nome do vendedor", example = "João da Praia")
    private String nomeVendedor;

    @Schema(description = "Latitude atual do vendedor", example = "-23.5614750")
    private BigDecimal latitude;

    @Schema(description = "Longitude atual do vendedor", example = "-46.6558830")
    private BigDecimal longitude;

    public static VendedorLocalizacaoOutputDto fromEntities(Vendedor vendedor, Localizacao localizacao) {
        return VendedorLocalizacaoOutputDto.builder()
                .idVendedor(vendedor.getId())
                .nomeVendedor(vendedor.getUsuario().getNome())
                .latitude(localizacao != null ? localizacao.getLatitude() : new BigDecimal("0"))
                .longitude(localizacao != null ? localizacao.getLongitude() : new BigDecimal("0"))
                .build();
    }
}

package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.Praia;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PraiaOutputDto {

    @Schema(description = "Identificador da praia", example = "1")
    private Long id;

    @Schema(description = "Nome da praia", example = "Praia de Boa Viagem")
    private String nome;

    @Schema(description = "URL da foto da praia", example = "https://exemplo.com/boa-viagem.jpg")
    private String urlFoto;

    @Schema(description = "Cidade onde a praia está localizada", example = "Recife")
    private String cidade;

    @Schema(description = "Sigla do estado onde a praia está localizada", example = "PE")
    private String estado;

    @Schema(description = "Quantidade de vendedores online na praia", example = "5")
    private Integer qtdVendedoresOnline;

    public static PraiaOutputDto fromEntity(Praia praia) {
        return PraiaOutputDto.builder()
                .id(praia.getId())
                .nome(praia.getNome())
                .urlFoto(praia.getUrlFoto())
                .cidade(praia.getCidade())
                .estado(praia.getEstado())
                .build();
    }
}

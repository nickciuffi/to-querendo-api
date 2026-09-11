package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.Localizacao;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocalizacaoOutputDto {

    @Schema(description = "Identificador do usuário dono da localização", example = "1")
    private Long idUsuario;

    @Schema(description = "Latitude da localização atual do usuário", example = "-23.5505199")
    private BigDecimal latitude;

    @Schema(description = "Longitude da localização atual do usuário", example = "-46.6333094")
    private BigDecimal longitude;

    public static LocalizacaoOutputDto fromEntity(Localizacao localizacao) {
        return LocalizacaoOutputDto.builder()
                .idUsuario(localizacao.getIdUsuario())
                .latitude(localizacao.getLatitude())
                .longitude(localizacao.getLongitude())
                .build();
    }
}

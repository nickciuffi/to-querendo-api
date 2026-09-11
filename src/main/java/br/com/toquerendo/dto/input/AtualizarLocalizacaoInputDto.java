package br.com.toquerendo.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AtualizarLocalizacaoInputDto {

    @Schema(description = "Latitude da localização atual do usuário", example = "-23.5505199", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @DecimalMin(value = "-90", message = "Latitude deve ser maior ou igual a -90")
    @DecimalMax(value = "90", message = "Latitude deve ser menor ou igual a 90")
    private BigDecimal latitude;

    @Schema(description = "Longitude da localização atual do usuário", example = "-46.6333094", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @DecimalMin(value = "-180", message = "Longitude deve ser maior ou igual a -180")
    @DecimalMax(value = "180", message = "Longitude deve ser menor ou igual a 180")
    private BigDecimal longitude;
}

package br.com.toquerendo.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AtualizarPraiaInputDto {

    @Schema(description = "Nome da praia", example = "Praia de Boa Viagem")
    private String nome;

    @Schema(description = "URL da foto da praia", example = "https://exemplo.com/boa-viagem.jpg")
    private String urlFoto;

    @Schema(description = "Cidade onde a praia está localizada", example = "Recife")
    private String cidade;

    @Schema(description = "Sigla do estado onde a praia está localizada", example = "PE")
    @Size(min = 2, max = 2)
    private String estado;

    @Schema(description = "Latitude da praia", example = "-8.1189")
    @DecimalMin(value = "-90", message = "Latitude deve ser maior ou igual a -90")
    @DecimalMax(value = "90", message = "Latitude deve ser menor ou igual a 90")
    @NotNull(message = "Latitude é obrigatória")
    private BigDecimal latitude;

    @Schema(description = "Longitude da praia", example = "-34.8998")
    @DecimalMin(value = "-180", message = "Longitude deve ser maior ou igual a -180")
    @DecimalMax(value = "180", message = "Longitude deve ser menor ou igual a 180")
    @NotNull(message = "Longitude é obrigatória")
    private BigDecimal longitude;
}

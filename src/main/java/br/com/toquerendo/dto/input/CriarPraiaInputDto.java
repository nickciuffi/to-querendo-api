package br.com.toquerendo.dto.input;

import br.com.toquerendo.entity.Praia;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CriarPraiaInputDto {

    @Schema(description = "Nome da praia", example = "Praia de Boa Viagem", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String nome;

    @Schema(description = "URL da foto da praia", example = "https://exemplo.com/boa-viagem.jpg")
    private String urlFoto;

    @Schema(description = "Cidade onde a praia está localizada", example = "Recife", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String cidade;

    @Schema(description = "Sigla do estado onde a praia está localizada", example = "PE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 2, max = 2)
    private String estado;

    @Schema(description = "Latitude da praia", example = "-8.1189")
    @DecimalMin(value = "-90", message = "Latitude deve ser maior ou igual a -90")
    @DecimalMax(value = "90", message = "Latitude deve ser menor ou igual a 90")
    @NotEmpty(message = "Latitude é obrigatória")
    private BigDecimal latitude;

    @Schema(description = "Longitude da praia", example = "-34.8998")
    @DecimalMin(value = "-180", message = "Longitude deve ser maior ou igual a -180")
    @DecimalMax(value = "180", message = "Longitude deve ser menor ou igual a 180")
    @NotEmpty(message = "Longitude é obrigatória")
    private BigDecimal longitude;

    public static Praia toEntity(CriarPraiaInputDto dto) {
        Praia praia = new Praia();
        praia.setNome(dto.getNome());
        praia.setUrlFoto(dto.getUrlFoto());
        praia.setCidade(dto.getCidade());
        praia.setEstado(dto.getEstado());
        praia.setLatitude(dto.getLatitude());
        praia.setLongitude(dto.getLongitude());
        return praia;
    }
}

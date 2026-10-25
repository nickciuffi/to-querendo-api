package br.com.toquerendo.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConcluirIntencaoCompraInputDto {

    @Schema(description = "Identificador do produto base da intenção de compra a ser concluída", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "O id do produto base é obrigatório")
    private Long idProdutoBase;

    @Schema(description = "Motivo da conclusão da intenção de compra", example = "Já comprei de outro vendedor", maxLength = 200, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O motivo da conclusão é obrigatório")
    @Size(max = 200, message = "O motivo da conclusão deve ter no máximo 200 caracteres")
    private String motivo;
}

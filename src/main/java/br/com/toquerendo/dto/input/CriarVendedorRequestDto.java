package br.com.toquerendo.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CriarVendedorRequestDto {

    @Schema(description = "Descrição do vendedor, exibida aos clientes", example = "Vendedor de picolés artesanais na orla")
    @Size(max = 500, message = "descricao deve ter no máximo 500 caracteres")
    private String descricao;
}

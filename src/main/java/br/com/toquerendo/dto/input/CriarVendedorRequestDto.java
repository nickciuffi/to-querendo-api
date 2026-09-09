package br.com.toquerendo.dto.input;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CriarVendedorRequestDto {

    @Size(max = 500, message = "descricao deve ter no máximo 500 caracteres")
    private String descricao;
}

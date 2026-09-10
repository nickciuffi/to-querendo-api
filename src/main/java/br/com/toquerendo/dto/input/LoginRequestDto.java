package br.com.toquerendo.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @Schema(description = "Email do usuário cadastrado", example = "usuario@email.com")
    @NotBlank(message = "email é obrigatório")
    private String email;

    @Schema(description = "Senha do usuário", example = "senha123")
    @NotBlank(message = "senha é obrigatória")
    private String senha;
}

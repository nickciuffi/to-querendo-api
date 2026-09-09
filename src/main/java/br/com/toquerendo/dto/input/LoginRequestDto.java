package br.com.toquerendo.dto.input;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank(message = "email é obrigatório")
    private String email;

    @NotBlank(message = "senha é obrigatória")
    private String senha;
}

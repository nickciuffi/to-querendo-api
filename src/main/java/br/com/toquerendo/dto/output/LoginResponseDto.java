package br.com.toquerendo.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {

    @Schema(description = "Token JWT de acesso", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Tipo do token, a ser usado no header Authorization", example = "Bearer")
    private String tipo;

    @Schema(description = "Tempo de expiração do token em milissegundos", example = "3600000")
    private long expiraEmMs;
}

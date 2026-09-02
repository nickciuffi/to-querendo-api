package br.com.toquerendo.dto.output;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    private String token;
    private String tipo;
    private long expiraEmMs;
}

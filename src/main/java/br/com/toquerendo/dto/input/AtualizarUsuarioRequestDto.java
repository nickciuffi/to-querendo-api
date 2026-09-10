package br.com.toquerendo.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AtualizarUsuarioRequestDto {

    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    private String nome;

    @Schema(description = "Telefone de contato do usuário", example = "11999999999")
    private String telefone;

    @Schema(description = "CPF do usuário, apenas dígitos", example = "12345678900")
    private String cpf;

    @Schema(description = "URL da foto de perfil do usuário", example = "https://exemplo.com/foto.jpg")
    private String urlFoto;

    @Schema(description = "ID da praia a qual o usuário está associado", example = "1")
    private Long idPraia;
}

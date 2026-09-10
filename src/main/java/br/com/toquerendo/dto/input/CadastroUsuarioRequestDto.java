package br.com.toquerendo.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CadastroUsuarioRequestDto {

    @Schema(description = "Email do usuário, utilizado para login", example = "usuario@email.com")
    @NotBlank(message = "email é obrigatório")
    @Email(message = "email deve ser válido")
    private String email;

    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    @NotBlank(message = "nome é obrigatório")
    private String nome;

    @Schema(description = "Senha de acesso, mínimo de 6 caracteres", example = "senha123")
    @NotBlank(message = "senha é obrigatória")
    @Size(min = 6, message = "senha deve ter no mínimo 6 caracteres")
    private String senha;

    @Schema(description = "Telefone de contato do usuário", example = "11999999999")
    private String telefone;

    @Schema(description = "CPF do usuário, apenas dígitos", example = "12345678900")
    @Pattern(regexp = "\\d{11}", message = "cpf deve conter 11 dígitos numéricos")
    private String cpf;
}

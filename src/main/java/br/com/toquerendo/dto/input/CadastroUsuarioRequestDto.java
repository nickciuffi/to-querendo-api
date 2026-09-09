package br.com.toquerendo.dto.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CadastroUsuarioRequestDto {

    @NotBlank(message = "email é obrigatório")
    @Email(message = "email deve ser válido")
    private String email;

    @NotBlank(message = "nome é obrigatório")
    private String nome;

    @NotBlank(message = "senha é obrigatória")
    @Size(min = 6, message = "senha deve ter no mínimo 6 caracteres")
    private String senha;

    private String telefone;

    @Pattern(regexp = "\\d{11}", message = "cpf deve conter 11 dígitos numéricos")
    private String cpf;
}

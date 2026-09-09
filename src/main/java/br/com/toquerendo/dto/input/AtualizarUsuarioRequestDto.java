package br.com.toquerendo.dto.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AtualizarUsuarioRequestDto {

    @NotBlank(message = "email é obrigatório")
    @Email(message = "email deve ser válido")
    private String email;

    @NotBlank(message = "nome é obrigatório")
    private String nome;

    private String telefone;

    private String urlFoto;

    @Size(min = 6, message = "senha deve ter no mínimo 6 caracteres")
    private String senha;
}

package br.com.toquerendo.dto.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AtualizarUsuarioRequestDto {

    private String nome;

    private String telefone;

    private String cpf;

    private String urlFoto;
}

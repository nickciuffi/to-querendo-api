package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.Usuario;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioOutputDto {

    @Schema(description = "Email do usuário", example = "usuario@email.com")
    private String email;

    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    private String nome;

    @Schema(description = "Telefone de contato do usuário", example = "11999999999")
    private String telefone;

    @Schema(description = "CPF do usuário", example = "12345678900")
    private String cpf;

    @Schema(description = "Indica se a conta do usuário está ativa", example = "true")
    private Boolean contaAtiva;

    public static UsuarioOutputDto fromEntity(Usuario usuario) {
        return UsuarioOutputDto.builder()
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .telefone(usuario.getTelefone())
                .cpf(usuario.getCpf())
                .contaAtiva(usuario.getContaAtiva())
                .build();
    }
}

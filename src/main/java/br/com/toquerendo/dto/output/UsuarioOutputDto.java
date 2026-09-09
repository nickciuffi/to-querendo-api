package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.Usuario;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioOutputDto {
    private String email;
    private String nome;
    private String telefone;
    private String cpf;
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

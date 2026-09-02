package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.Usuario;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UsuarioOutputDto {
    private Long id;
    private String email;
    private String nome;
    private String telefone;
    private String cpf;
    private LocalDateTime tsCriacaoConta;
    private Boolean contaAtiva;

    public static UsuarioOutputDto fromEntity(Usuario usuario) {
        return UsuarioOutputDto.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .telefone(usuario.getTelefone())
                .cpf(usuario.getCpf())
                .tsCriacaoConta(usuario.getTsCriacaoConta())
                .contaAtiva(usuario.getContaAtiva())
                .build();
    }
}

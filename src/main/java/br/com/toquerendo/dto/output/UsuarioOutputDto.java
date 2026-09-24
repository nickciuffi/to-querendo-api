package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.Usuario;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UsuarioOutputDto {

    @Schema(description = "Email do usuário", example = "usuario@email.com")
    private String email;

    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    private String nome;

    @Schema(description = "CPF do usuário", example = "12345678900")
    private String cpf;

    @Schema(description = "Telefone de contato do usuário", example = "11999999999")
    private String telefone;

    @Schema(description = "URL da foto de perfil do usuário", example = "https://exemplo.com/foto.jpg")
    private String urlFoto;

    @Schema(description = "Praia atual do usuário", example = "Praia do Forte")
    private PraiaOutputDto praiaAtual;

    @Schema(description = "Categoria do usuário", example = "Vendedor")
    private CategoriaOutputDto categoria;

    public static UsuarioOutputDto fromEntity(Usuario usuario) {
        return UsuarioOutputDto.builder()
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .telefone(usuario.getTelefone())
                .cpf(usuario.getCpf())
                .urlFoto(usuario.getUrlFoto())
                .praiaAtual(PraiaOutputDto.fromEntity(usuario.getPraia()))
                .categoria(CategoriaOutputDto.fromEntity(usuario.getCategoria()))
                .build();
    }
}

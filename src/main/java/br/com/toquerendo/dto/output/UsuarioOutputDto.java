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

    @Schema(description = "CPF do usuário", example = "12345678900")
    private String cpf;

    @Schema(description = "Telefone de contato do usuário", example = "11999999999")
    private String telefone;

    @Schema(description = "URL da foto de perfil do usuário", example = "https://exemplo.com/foto.jpg")
    private String urlFoto;

    @Schema(description = "Praia atual do usuário", example = "Praia do Forte")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private PraiaOutputDto praiaAtual;

    @Schema(description = "Categoria do usuário", example = "Vendedor")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private CategoriaOutputDto categoria;

    @Schema(description = "Refresh de token com a role de vendedor")
    private String token;

    @Schema(description = "Descrição do vendedor, exibida aos clientes", example = "Vendedor de picolés artesanais na orla")
    private String descricao;

    @Schema(description = "Indica se o vendedor está online e disponível para venda", example = "false")
    private Boolean online;

    public static UsuarioOutputDto fromEntity(Usuario usuario) {
        return UsuarioOutputDto.builder()
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .telefone(usuario.getTelefone() != null ? usuario.getTelefone() : "")
                .cpf(usuario.getCpf() != null ? usuario.getCpf() : "")
                .urlFoto(usuario.getUrlFoto() != null ? usuario.getUrlFoto() : "")
                .praiaAtual(PraiaOutputDto.fromEntity(usuario.getPraia()))
                .categoria(CategoriaOutputDto.fromEntity(usuario.getCategoria()))
                .build();
    }
}

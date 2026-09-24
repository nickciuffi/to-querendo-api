package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.Vendedor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpgradeVendedorOutputDto {

    @Schema(description = "Email do ao vendedor", example = "usuario@email.com")
    private String email;

    @Schema(description = "Nome do ao vendedor", example = "João da Silva")
    private String nome;

    @Schema(description = "CPF do vendedor", example = "12345678900")
    private String cpf;

    @Schema(description = "Telefone de contato do vendedor", example = "11999999999")
    private String telefone;

    @Schema(description = "URL da foto de perfil do vendedor", example = "https://exemplo.com/foto.jpg")
    private String urlFoto;

    @Schema(description = "Praia onde o vendedor está ativo", example = "Praia do Forte")
    private PraiaOutputDto praiaAtual;

    @Schema(description = "Categoria do usuário", example = "Vendedor")
    private CategoriaOutputDto categoria;

    @Schema(description = "Refresh de token com a role de vendedor")
    private String token;

    @Schema(description = "Descrição do vendedor, exibida aos clientes", example = "Vendedor de picolés artesanais na orla")
    private String descricao;

    @Schema(description = "Indica se o vendedor está online e disponível para venda", example = "false")
    private Boolean online;

    public static UpgradeVendedorOutputDto fromEntity(Vendedor vendedor) {
        return UpgradeVendedorOutputDto.builder()
                .descricao(vendedor.getDescricao())
                .online(vendedor.getOnline())
                .email(vendedor.getUsuario().getEmail())
                .nome(vendedor.getUsuario().getNome())
                .cpf(vendedor.getUsuario().getCpf())
                .telefone(vendedor.getUsuario().getTelefone())
                .urlFoto(vendedor.getUsuario().getUrlFoto())
                .praiaAtual(PraiaOutputDto.fromEntity(vendedor.getUsuario().getPraia()))
                .categoria(CategoriaOutputDto.fromEntity(vendedor.getUsuario().getCategoria()))
                .build();
    }
}

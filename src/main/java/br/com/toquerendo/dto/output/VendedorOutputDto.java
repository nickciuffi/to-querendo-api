package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.Vendedor;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendedorOutputDto {

    @Schema(description = "Identificador do vendedor", example = "1")
    private Long id;

    @Schema(description = "Descrição do vendedor, exibida aos clientes", example = "Vendedor de picolés artesanais na orla")
    private String descricao;

    @Schema(description = "Indica se o vendedor está online e disponível para venda", example = "false")
    private Boolean online;

    @Schema(description = "Email do usuário associado ao vendedor", example = "usuario@email.com")
    private String usuarioEmail;

    @Schema(description = "Nome do usuário associado ao vendedor", example = "João da Silva")
    private String usuarioNome;

    @Schema(description = "Quantidade de produtos do vendedor", example = "10")
    private Integer qtdProdutos;

    @Schema(description = "Quantidade de produtos ativos do vendedor", example = "10")
    private Integer qtdProdutosAtivos;

    @Schema(description = "Praia onde o vendedor está ativo", example = "Praia do Forte")
    private String praiaAtual;

    public static VendedorOutputDto fromEntity(Vendedor vendedor) {
        return VendedorOutputDto.builder()
                .id(vendedor.getId())
                .descricao(vendedor.getDescricao())
                .online(vendedor.getOnline())
                .usuarioEmail(vendedor.getUsuario().getEmail())
                .usuarioNome(vendedor.getUsuario().getNome())
                .build();
    }
}

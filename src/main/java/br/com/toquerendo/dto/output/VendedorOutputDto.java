package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.Vendedor;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendedorOutputDto {
    private Long id;
    private String descricao;
    private Boolean online;
    private String usuarioEmail;
    private String usuarioNome;

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

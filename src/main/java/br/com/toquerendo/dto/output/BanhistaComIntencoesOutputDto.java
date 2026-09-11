package br.com.toquerendo.dto.output;

import br.com.toquerendo.entity.IntencaoCompra;
import br.com.toquerendo.entity.Usuario;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BanhistaComIntencoesOutputDto {

    @Schema(description = "Identificador do usuário banhista", example = "1")
    private Long idUsuario;

    @Schema(description = "Nome do usuário banhista", example = "Maria Turista")
    private String nomeUsuario;

    @Schema(description = "Intenções de compra do banhista compatíveis com os produtos ativos do vendedor logado")
    private List<IntencaoCompraOutputDto> intencoesCompra;

    public static BanhistaComIntencoesOutputDto fromEntities(Usuario usuario, List<IntencaoCompra> intencoesCompra) {
        return BanhistaComIntencoesOutputDto.builder()
                .idUsuario(usuario.getId())
                .nomeUsuario(usuario.getNome())
                .intencoesCompra(intencoesCompra.stream()
                        .map(IntencaoCompraOutputDto::fromEntity)
                        .toList())
                .build();
    }
}

package br.com.toquerendo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class PaginacaoDTO<T> {

    @Schema(description = "Página atual na paginação", example = "1")
    private Integer pagina;

    @Schema(description = "Quantidade total de registros encontrados", example = "10")
    private Integer qtdRegistros;

    @Schema(description = "Quantidade de registros em cada página", example = "2")
    private Integer qtdRegistrosPagina;

    @Schema(description = "Dados da página atual")
    private List<T> registros;
}

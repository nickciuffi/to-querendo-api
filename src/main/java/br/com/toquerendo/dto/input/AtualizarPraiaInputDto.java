package br.com.toquerendo.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AtualizarPraiaInputDto {

    @Schema(description = "Nome da praia", example = "Praia de Boa Viagem")
    private String nome;

    @Schema(description = "URL da foto da praia", example = "https://exemplo.com/boa-viagem.jpg")
    private String urlFoto;

    @Schema(description = "Cidade onde a praia está localizada", example = "Recife")
    private String cidade;

    @Schema(description = "Sigla do estado onde a praia está localizada", example = "PE")
    @Size(min = 2, max = 2)
    private String estado;
}

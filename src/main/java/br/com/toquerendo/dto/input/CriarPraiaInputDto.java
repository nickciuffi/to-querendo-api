package br.com.toquerendo.dto.input;

import br.com.toquerendo.entity.Praia;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CriarPraiaInputDto {

    @Schema(description = "Nome da praia", example = "Praia de Boa Viagem", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String nome;

    @Schema(description = "URL da foto da praia", example = "https://exemplo.com/boa-viagem.jpg")
    private String urlFoto;

    @Schema(description = "Cidade onde a praia está localizada", example = "Recife", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String cidade;

    @Schema(description = "Sigla do estado onde a praia está localizada", example = "PE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 2, max = 2)
    private String estado;

    public static Praia toEntity(CriarPraiaInputDto dto) {
        Praia praia = new Praia();
        praia.setNome(dto.getNome());
        praia.setUrlFoto(dto.getUrlFoto());
        praia.setCidade(dto.getCidade());
        praia.setEstado(dto.getEstado());
        return praia;
    }
}

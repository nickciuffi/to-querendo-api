package br.com.toquerendo.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArquivoUploadOutputDto {

    @Schema(description = "Caminho do arquivo no bucket, iniciando por '/'. É este valor que deve ser enviado aos demais endpoints (urlFoto, urlFotoLocal).", example = "/usuarios/3f2c9a64-1d7e-4b8e-9f0a-5c1d2e3f4a5b.jpeg")
    private String path;

    @Schema(description = "URL pública completa do arquivo (URL base do bucket + path). Ausente se a URL base não estiver configurada.", example = "https://meu-bucket.s3.sa-east-1.amazonaws.com/usuarios/3f2c9a64-1d7e-4b8e-9f0a-5c1d2e3f4a5b.jpeg")
    private String url;
}

package br.com.toquerendo.controller;

import br.com.toquerendo.dto.ApiResponse;
import br.com.toquerendo.dto.output.ArquivoUploadOutputDto;
import br.com.toquerendo.service.ArquivoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/arquivo")
@AllArgsConstructor
@Tag(name = "Arquivos", description = "Upload de imagens para o armazenamento (S3)")
public class ArquivoController {

    private final ArquivoService arquivoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Enviar imagem",
            description = "Envia uma imagem (JPEG, PNG ou WEBP, até 5 MB) para o S3 e devolve o path do arquivo. "
                    + "O nome do arquivo é gerado pelo servidor. Envie o path retornado nos demais endpoints "
                    + "(urlFoto, urlFotoLocal). Pastas aceitas: usuarios, intencoes-compra (qualquer usuário autenticado), "
                    + "produtos-especificos (vendedor), produtos-base e praias (administrador)."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Arquivo enviado com sucesso"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Pasta inválida, arquivo ausente/vazio ou tipo não suportado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token ausente, inválido ou expirado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Usuário sem permissão para enviar arquivos para a pasta informada",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "413",
                    description = "Arquivo maior que o limite permitido (5 MB)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Falha ao gravar o arquivo no armazenamento",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<ArquivoUploadOutputDto>> upload(
            @Parameter(description = "Pasta de destino: usuarios, intencoes-compra, produtos-especificos, produtos-base ou praias", required = true)
            @RequestParam("pasta") String pasta,
            @Parameter(description = "Imagem a ser enviada (JPEG, PNG ou WEBP)", required = true)
            @RequestPart("arquivo") MultipartFile arquivo) {
        ArquivoUploadOutputDto output = arquivoService.upload(pasta, arquivo);
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Arquivo enviado com sucesso!"));
    }
}

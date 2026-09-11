package br.com.toquerendo.controller;

import br.com.toquerendo.dto.ApiResponse;
import br.com.toquerendo.dto.input.CriarIntencaoCompraInputDto;
import br.com.toquerendo.dto.output.IntencaoCompraOutputDto;
import br.com.toquerendo.security.annotation.TuristaOnly;
import br.com.toquerendo.service.implementation.IntencaoCompraServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/intencao-compra")
@AllArgsConstructor
@Tag(name = "Intenções de Compra", description = "Cadastro de intenções de compra dos turistas para os produtos base")
public class IntencaoCompraController {

    private final IntencaoCompraServiceImpl intencaoCompraService;

    @PostMapping("")
    @TuristaOnly
    @Operation(
            summary = "Criar intenção de compra",
            description = "Registra a intenção de compra do turista autenticado para um produto base existente. "
                    + "Restrito a usuários com a role de turista."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Intenção de compra criada com sucesso"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos",
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
                    description = "Usuário autenticado não possui a role de turista",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Produto base não encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<IntencaoCompraOutputDto>> criarIntencaoCompra(
            @RequestBody @Valid CriarIntencaoCompraInputDto intencaoCompra) {
        IntencaoCompraOutputDto output = intencaoCompraService.criarIntencaoCompra(intencaoCompra);
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Intenção de compra criada com sucesso!"));
    }
}

package br.com.toquerendo.controller;

import br.com.toquerendo.dto.ApiResponse;
import br.com.toquerendo.dto.input.AtualizarPraiaInputDto;
import br.com.toquerendo.dto.input.CriarPraiaInputDto;
import br.com.toquerendo.dto.output.PraiaOutputDto;
import br.com.toquerendo.security.annotation.AdminOnly;
import br.com.toquerendo.service.implementation.PraiaServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/praia")
@AllArgsConstructor
@Tag(name = "Praias", description = "Consulta e gestão das praias cadastradas na plataforma")
public class PraiaController {

    private final PraiaServiceImpl praiaService;

    @GetMapping("")
    @Operation(
            summary = "Listar praias",
            description = "Retorna todas as praias cadastradas. Requer usuário autenticado, disponível para todas as roles."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Praias consultadas com sucesso"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token ausente, inválido ou expirado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<List<PraiaOutputDto>>> consultarPraias() {
        List<PraiaOutputDto> praias = praiaService.consultarPraias();
        return ResponseEntity.ok().body(new ApiResponse<>(praias, "Praias consultadas com sucesso!"));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar praia por identificador",
            description = "Retorna os dados de uma praia a partir do seu identificador. Requer usuário autenticado, disponível para todas as roles."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Praia consultada com sucesso"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token ausente, inválido ou expirado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Praia não encontrada",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<PraiaOutputDto>> consultarPraiaPorId(
            @Parameter(description = "Identificador da praia", required = true) @PathVariable Long id) {
        PraiaOutputDto output = praiaService.consultarPraiaPorId(id);
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Praia consultada com sucesso!"));
    }

    @PostMapping("")
    @AdminOnly
    @Operation(
            summary = "Criar praia",
            description = "Cadastra uma nova praia na plataforma. Restrito a administradores."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Praia criada com sucesso"
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
                    description = "Usuário autenticado não possui a role de administrador",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<PraiaOutputDto>> criarPraia(@RequestBody @Valid CriarPraiaInputDto praia) {
        PraiaOutputDto output = praiaService.criarPraia(praia);
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Praia criada com sucesso!"));
    }

    @PutMapping("/{id}")
    @AdminOnly
    @Operation(
            summary = "Atualizar praia",
            description = "Atualiza os dados de uma praia existente a partir do seu identificador. Restrito a administradores."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Praia atualizada com sucesso"
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
                    description = "Usuário autenticado não possui a role de administrador",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Praia não encontrada",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<PraiaOutputDto>> atualizarPraia(
            @Parameter(description = "Identificador da praia", required = true) @PathVariable Long id,
            @RequestBody AtualizarPraiaInputDto praia) {
        PraiaOutputDto output = praiaService.atualizarPraia(id, praia);
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Praia atualizada com sucesso!"));
    }

    @DeleteMapping("/{id}")
    @AdminOnly
    @Operation(
            summary = "Remover praia",
            description = "Remove uma praia a partir do seu identificador. Restrito a administradores."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Praia removida com sucesso"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token ausente, inválido ou expirado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Usuário autenticado não possui a role de administrador",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Praia não encontrada",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<Object>> deletarPraia(
            @Parameter(description = "Identificador da praia", required = true) @PathVariable Long id) {
        praiaService.deletarPraia(id);
        return ResponseEntity.ok().body(new ApiResponse<>("Praia removida com sucesso!"));
    }
}

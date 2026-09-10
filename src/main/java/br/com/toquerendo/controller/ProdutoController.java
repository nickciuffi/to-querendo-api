package br.com.toquerendo.controller;

import br.com.toquerendo.dto.ApiResponse;
import br.com.toquerendo.dto.input.AtualizarProdutoBaseInputDto;
import br.com.toquerendo.dto.input.CriarProdutoBaseInputDto;
import br.com.toquerendo.dto.output.ProdutoBaseOutputDto;
import br.com.toquerendo.security.annotation.AdminOnly;
import br.com.toquerendo.service.implementation.ProdutoBaseServiceImpl;
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
@RequestMapping("/produto-base")
@AllArgsConstructor
@Tag(name = "Produtos", description = "Consulta e gestão dos produtos base oferecidos na plataforma")
public class ProdutoController {

    private final ProdutoBaseServiceImpl produtoBaseService;

    @GetMapping("")
    @Operation(
            summary = "Listar produtos disponíveis",
            description = "Retorna os produtos base ativos, disponíveis para venda. Requer usuário autenticado."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Produtos consultados com sucesso"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token ausente, inválido ou expirado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<List<ProdutoBaseOutputDto>>> consultarProdutosDisponiveis() {
        List<ProdutoBaseOutputDto> prodsOutput = produtoBaseService.consultarProdutosBaseAtivos();
        return ResponseEntity.ok().body(new ApiResponse<>(prodsOutput, "Produtos consultados com sucesso!"));
    }

    @GetMapping("/todos")
    @AdminOnly
    @Operation(
            summary = "Listar todos os produtos",
            description = "Retorna todos os produtos base cadastrados, ativos e inativos. Restrito a administradores."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Produtos consultados com sucesso"
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
    public ResponseEntity<ApiResponse<List<ProdutoBaseOutputDto>>> consultarTodosProdutos() {
        List<ProdutoBaseOutputDto> prodsOutput = produtoBaseService.consultarProdutosBase();
        return ResponseEntity.ok().body(new ApiResponse<>(prodsOutput, "Produtos consultados com sucesso!"));
    }

    @PostMapping("")
    @AdminOnly
    @Operation(
            summary = "Criar produto base",
            description = "Cadastra um novo produto base na plataforma. Restrito a administradores."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Produto criado com sucesso"
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
    public ResponseEntity<ApiResponse<ProdutoBaseOutputDto>> criarProdutoBase(@RequestBody @Valid CriarProdutoBaseInputDto produtoBase) {
        ProdutoBaseOutputDto output = produtoBaseService.criarProdutoBase(produtoBase);

        return ResponseEntity.ok().body(new ApiResponse<>(output, "Produto criado com sucesso!"));
    }

    @DeleteMapping("/{id}")
    @AdminOnly
    @Operation(
            summary = "Remover produto base",
            description = "Remove (ou inativa) um produto base a partir do seu identificador. Restrito a administradores."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Produto removido com sucesso"
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
                    description = "Produto não encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<Object>> deletarProdutoBase(
            @Parameter(description = "Identificador do produto base", required = true) @PathVariable Long id) {
        produtoBaseService.deletarProdutoBase(id);
        return ResponseEntity.ok().body(new ApiResponse<>("Produto removido com sucesso!"));
    }

    @PutMapping("/{id}")
    @AdminOnly
    @Operation(
            summary = "Atualizar produto base",
            description = "Atualiza os dados de um produto base existente a partir do seu identificador. Restrito a administradores."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Produto atualizado com sucesso"
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
                    description = "Produto não encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<ProdutoBaseOutputDto>> atualizarProdutoBase(
            @Parameter(description = "Identificador do produto base", required = true) @PathVariable Long id,
            @RequestBody AtualizarProdutoBaseInputDto produtoBase) {
        ProdutoBaseOutputDto output = produtoBaseService.atualizarProdutoBase(id, produtoBase);
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Produto atualizado com sucesso!"));
    }
}

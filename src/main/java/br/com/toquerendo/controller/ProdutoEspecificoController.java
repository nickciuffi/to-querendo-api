package br.com.toquerendo.controller;

import br.com.toquerendo.dto.ApiResponse;
import br.com.toquerendo.dto.input.AtualizarProdutoEspecificoInputDto;
import br.com.toquerendo.dto.input.CriarProdutoEspecificoInputDto;
import br.com.toquerendo.dto.output.ProdutoEspecificoOutputDto;
import br.com.toquerendo.security.annotation.VendedorOnly;
import br.com.toquerendo.service.implementation.ProdutoEspecificoServiceImpl;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/produto-especifico")
@AllArgsConstructor
@Tag(name = "Produtos Específicos", description = "Cadastro e gestão dos produtos oferecidos pelos vendedores")
public class ProdutoEspecificoController {

    private final ProdutoEspecificoServiceImpl produtoEspecificoService;

    @PostMapping("")
    @VendedorOnly
    @Operation(
            summary = "Criar produto específico",
            description = "Cadastra um novo produto para o vendedor autenticado, associado a um produto base existente. "
                    + "Restrito a usuários com a role de vendedor."
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
                    description = "Usuário autenticado não possui a role de vendedor",
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
    public ResponseEntity<ApiResponse<ProdutoEspecificoOutputDto>> criarProdutoEspecifico(
            @RequestBody @Valid CriarProdutoEspecificoInputDto produtoEspecifico) {
        ProdutoEspecificoOutputDto output = produtoEspecificoService.criarProdutoEspecifico(produtoEspecifico);
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Produto criado com sucesso!"));
    }

    @PutMapping("/{id}")
    @VendedorOnly
    @Operation(
            summary = "Atualizar produto específico",
            description = "Atualiza os dados de um produto do vendedor autenticado. Somente o vendedor dono do produto "
                    + "pode realizar esta operação."
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
                    description = "Usuário autenticado não possui a role de vendedor ou não é o dono do produto",
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
    public ResponseEntity<ApiResponse<ProdutoEspecificoOutputDto>> atualizarProdutoEspecifico(
            @Parameter(description = "Identificador do produto específico", required = true) @PathVariable Long id,
            @RequestBody AtualizarProdutoEspecificoInputDto produtoEspecifico) {
        ProdutoEspecificoOutputDto output = produtoEspecificoService.atualizarProdutoEspecifico(id, produtoEspecifico);
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Produto atualizado com sucesso!"));
    }

    @GetMapping("/meus-produtos")
    @VendedorOnly
    @Operation(
            summary = "Listar produtos ativos do vendedor autenticado",
            description = "Retorna os produtos ativos cadastrados pelo vendedor autenticado. Restrito a usuários com a role de vendedor."
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
                    description = "Usuário autenticado não possui a role de vendedor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<List<ProdutoEspecificoOutputDto>>> consultarProdutosAtivosDoVendedorLogado() {
        List<ProdutoEspecificoOutputDto> produtos = produtoEspecificoService.consultarProdutosAtivosDoVendedorLogado();
        return ResponseEntity.ok().body(new ApiResponse<>(produtos, "Produtos consultados com sucesso!"));
    }
}

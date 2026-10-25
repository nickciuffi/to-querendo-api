package br.com.toquerendo.controller;

import br.com.toquerendo.dto.ApiResponse;
import br.com.toquerendo.dto.input.CriarVendedorRequestDto;
import br.com.toquerendo.dto.output.UsuarioOutputDto;
import br.com.toquerendo.dto.output.VendedorLocalizacaoProdutosOutputDto;
import br.com.toquerendo.security.annotation.TuristaOnly;
import br.com.toquerendo.service.VendedorService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendedor")
@AllArgsConstructor
@Tag(name = "Vendedores", description = "gestão de usuários")
public class VendedorController {

    private VendedorService vendedorService;

    @PostMapping("")
    @Operation(
            summary = "Cadastrar vendedor",
            description = "Promove o usuário autenticado para a categoria de vendedor, permitindo a venda de produtos "
                    + "na plataforma. Requer usuário autenticado que ainda não seja vendedor."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Vendedor cadastrado com sucesso"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos ou usuário já é vendedor",
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
                    responseCode = "409",
                    description = "Usuário já cadastrado como vendedor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ResponseEntity<ApiResponse<UsuarioOutputDto>> cadastrarVendedor(@Valid @RequestBody CriarVendedorRequestDto criarVendedorRequest) {
        UsuarioOutputDto output = vendedorService.cadastrarVendedor(criarVendedorRequest);
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Vendedor cadastrado com sucesso!"));
    }

    @GetMapping("/localizacao")
    @TuristaOnly
    @Operation(
            summary = "Consultar localização dos vendedores online de uma praia, filtrando por produtos base",
            description = "Retorna a localização atual dos vendedores online da praia informada que possuam produto ativo de "
                    + "qualquer um dos produtos base informados em idsProdutosBase (ex.: \"3,2,6\"). Valores não numéricos e "
                    + "repetidos são ignorados. Se idsProdutosBase for omitido ou vazio, não filtra por produto base e retorna "
                    + "todos os vendedores online da praia com algum produto ativo. Restrito a usuários com a role de turista ou vendedor."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Localizações consultadas com sucesso"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token ausente, inválido ou expirado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Usuário autenticado não possui a role de turista ou vendedor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Produto base ou praia não encontrados",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<List<VendedorLocalizacaoProdutosOutputDto>>> consultarLocalizacaoVendedores(
            @Parameter(description = "Identificadores dos produtos base separados por vírgula (ex.: 3,2,6). Opcional.", required = false, example = "3,2,6") @RequestParam(required = false) String idsProdutosBase,
            @Parameter(description = "Identificador da praia", required = true) @RequestParam Long idPraia) {
        List<VendedorLocalizacaoProdutosOutputDto> vendedores =
                vendedorService.consultarLocalizacaoVendedores(idsProdutosBase, idPraia);
        return ResponseEntity.ok().body(new ApiResponse<>(vendedores, "Localizações consultadas com sucesso!"));
    }
}

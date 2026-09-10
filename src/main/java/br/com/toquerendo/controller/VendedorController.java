package br.com.toquerendo.controller;

import br.com.toquerendo.dto.ApiResponse;
import br.com.toquerendo.dto.input.CriarVendedorRequestDto;
import br.com.toquerendo.dto.output.VendedorOutputDto;
import br.com.toquerendo.service.implementation.VendedorServiceImpl;
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
@RequestMapping("/vendedor")
@AllArgsConstructor
@Tag(name = "Vendedores", description = "Upgrade de usuários para a categoria de vendedor")
public class VendedorController {

    private VendedorServiceImpl vendedorService;

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
    ResponseEntity<ApiResponse<VendedorOutputDto>> cadastrarVendedor(@Valid @RequestBody CriarVendedorRequestDto criarVendedorRequest) {
        VendedorOutputDto output = vendedorService.cadastrarVendedor(criarVendedorRequest);
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Vendedor cadastrado com sucesso!"));
    }
}

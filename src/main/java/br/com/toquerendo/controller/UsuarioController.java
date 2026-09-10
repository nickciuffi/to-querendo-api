package br.com.toquerendo.controller;

import br.com.toquerendo.dto.ApiResponse;
import br.com.toquerendo.dto.input.AtualizarUsuarioRequestDto;
import br.com.toquerendo.dto.input.CadastroUsuarioRequestDto;
import br.com.toquerendo.dto.output.UsuarioOutputDto;
import br.com.toquerendo.security.annotation.AdminOnly;
import br.com.toquerendo.service.implementation.UsuarioServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
@AllArgsConstructor
@Tag(name = "Usuários", description = "Cadastro e manutenção de dados dos usuários")
public class UsuarioController {

    private UsuarioServiceImpl usuarioService;

    @PostMapping("")
    @SecurityRequirements
    @Operation(
            summary = "Cadastrar usuário",
            description = "Cria um novo usuário (turista) na plataforma. Endpoint público, não requer autenticação."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Usuário cadastrado com sucesso"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Email ou CPF já cadastrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ResponseEntity<ApiResponse<UsuarioOutputDto>> cadastrarUsuario(@Valid @RequestBody CadastroUsuarioRequestDto cadastroRequest) {

        UsuarioOutputDto output = usuarioService.cadastrarUsuario(cadastroRequest);
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Usuário cadastrado com sucesso!"));
    }

    @PutMapping("")
    @Operation(
            summary = "Atualizar usuário autenticado",
            description = "Atualiza os dados cadastrais (nome, telefone, CPF e foto) do usuário autenticado. "
                    + "Requer token JWT válido."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Dados do usuário atualizados com sucesso"
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
            )
    })
    ResponseEntity<ApiResponse<UsuarioOutputDto>> atualizarUsuario(@Valid @RequestBody AtualizarUsuarioRequestDto atualizarUsuarioRequest) {
        UsuarioOutputDto output = usuarioService.editarUsuario(atualizarUsuarioRequest);
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Dados do usuário atualizados com sucesso!"));
    }

    @GetMapping("/meus-dados")
    @Operation(
            summary = "Consultar usuário autenticado",
            description = "Retorna os dados cadastrais do usuário autenticado, identificado pelo email presente no token JWT."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Dados do usuário consultados com sucesso"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token ausente, inválido ou expirado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ResponseEntity<ApiResponse<UsuarioOutputDto>> consultarUsuarioAutenticado() {
        UsuarioOutputDto output = usuarioService.consultarUsuarioAutenticado();
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Dados do usuário consultados com sucesso!"));
    }
}

package br.com.toquerendo.controller;

import br.com.toquerendo.dto.ApiResponse;
import br.com.toquerendo.dto.input.AtualizarLocalizacaoInputDto;
import br.com.toquerendo.dto.output.LocalizacaoOutputDto;
import br.com.toquerendo.dto.output.VendedorLocalizacaoOutputDto;
import br.com.toquerendo.security.annotation.TuristaOnly;
import br.com.toquerendo.service.implementation.LocalizacaoServiceImpl;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/localizacao")
@AllArgsConstructor
@Tag(name = "Localização", description = "Atualização e consulta da localização geográfica de usuários e vendedores")
public class LocalizacaoController {

    private final LocalizacaoServiceImpl localizacaoService;

    @PutMapping("")
    @TuristaOnly
    @Operation(
            summary = "Atualizar localização do usuário logado",
            description = "Registra ou atualiza a latitude e longitude atuais do usuário autenticado. "
                    + "Restrito a usuários com a role de turista ou vendedor."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Localização atualizada com sucesso"
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
                    description = "Usuário autenticado não possui a role de turista ou vendedor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<LocalizacaoOutputDto>> atualizarLocalizacao(
            @RequestBody @Valid AtualizarLocalizacaoInputDto localizacao) {
        LocalizacaoOutputDto output = localizacaoService.atualizarLocalizacao(localizacao);
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Localização atualizada com sucesso!"));
    }
}

package br.com.toquerendo.controller;

import br.com.toquerendo.dto.ApiResponse;
import br.com.toquerendo.dto.input.AtualizarUsuarioRequestDto;
import br.com.toquerendo.dto.input.CadastroUsuarioRequestDto;
import br.com.toquerendo.dto.output.UsuarioOutputDto;
import br.com.toquerendo.security.annotation.AdminOnly;
import br.com.toquerendo.service.implementation.UsuarioServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
public class UsuarioController {

    private UsuarioServiceImpl usuarioService;

    @PostMapping("")
    ResponseEntity<ApiResponse<UsuarioOutputDto>> cadastrarUsuario(@Valid @RequestBody CadastroUsuarioRequestDto cadastroRequest) {

        UsuarioOutputDto output = usuarioService.cadastrarUsuario(cadastroRequest);
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Usuário cadastrado com sucesso!"));
    }

    @PutMapping("")
    ResponseEntity<ApiResponse<UsuarioOutputDto>> atualizarUsuario(@Valid @RequestBody AtualizarUsuarioRequestDto atualizarUsuarioRequest) {
        UsuarioOutputDto output = usuarioService.editarUsuario(atualizarUsuarioRequest);
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Dados do usuário atualizados com sucesso!"));
    }
}


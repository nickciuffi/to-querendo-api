package br.com.toquerendo.controller;

import br.com.toquerendo.dto.ApiResponse;
import br.com.toquerendo.dto.input.CadastroUsuarioRequestDto;
import br.com.toquerendo.dto.output.UsuarioOutputDto;
import br.com.toquerendo.entity.Usuario;
import br.com.toquerendo.exception.CpfJaCadastradoException;
import br.com.toquerendo.exception.EmailJaCadastradoException;
import br.com.toquerendo.repository.UsuarioRepository;
import br.com.toquerendo.service.implementation.UsuarioServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

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
}

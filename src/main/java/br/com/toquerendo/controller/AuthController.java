package br.com.toquerendo.controller;

import br.com.toquerendo.dto.ApiResponse;
import br.com.toquerendo.dto.input.LoginRequestDto;
import br.com.toquerendo.dto.output.LoginResponseDto;
import br.com.toquerendo.entity.Usuario;
import br.com.toquerendo.exception.CredenciaisInvalidasException;
import br.com.toquerendo.repository.UsuarioRepository;
import br.com.toquerendo.security.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    @PostMapping("/login")
    ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(loginRequest.getSenha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException();
        }

        String token = jwtService.gerarToken(usuario.getEmail(), usuario.getCategoria().getId());
        LoginResponseDto response = LoginResponseDto.builder()
                .token(token)
                .tipo("Bearer")
                .expiraEmMs(jwtService.getExpiracaoMs())
                .build();

        return ResponseEntity.ok().body(new ApiResponse<>(response, "Login realizado com sucesso!"));
    }
}

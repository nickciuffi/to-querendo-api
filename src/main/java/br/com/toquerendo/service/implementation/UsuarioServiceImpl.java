package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.AtualizarUsuarioRequestDto;
import br.com.toquerendo.dto.input.CadastroUsuarioRequestDto;
import br.com.toquerendo.dto.output.UsuarioOutputDto;
import br.com.toquerendo.entity.Usuario;
import br.com.toquerendo.enums.CategoriaUsuarioEnum;
import br.com.toquerendo.exception.CpfJaCadastradoException;
import br.com.toquerendo.exception.EmailJaCadastradoException;
import br.com.toquerendo.exception.UsuarioNaoAutorizadoException;
import br.com.toquerendo.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UsuarioServiceImpl {

    private UsuarioRepository usuarioRepository;

    private PasswordEncoder passwordEncoder;

    public UsuarioOutputDto cadastrarUsuario(CadastroUsuarioRequestDto cadastroRequest) {

        if (usuarioRepository.existsByEmail(cadastroRequest.getEmail())) {
            throw new EmailJaCadastradoException();
        }
        if (cadastroRequest.getCpf() != null && usuarioRepository.existsByCpf(cadastroRequest.getCpf())) {
            throw new CpfJaCadastradoException();
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(cadastroRequest.getEmail());
        usuario.setNome(cadastroRequest.getNome());
        usuario.setSenha(passwordEncoder.encode(cadastroRequest.getSenha()));
        usuario.setTelefone(cadastroRequest.getTelefone());
        usuario.setCpf(cadastroRequest.getCpf());
        usuario.setTsCriacaoConta(LocalDateTime.now());
        usuario.setContaAtiva(true);
        usuario.setCategoriaId(CategoriaUsuarioEnum.TURISTA.getId());

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return UsuarioOutputDto.fromEntity(usuarioSalvo);
    }

    public UsuarioOutputDto editarUsuario(AtualizarUsuarioRequestDto atualizarUsuarioRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null
                || !authentication.getName().equalsIgnoreCase(atualizarUsuarioRequest.getEmail())) {
            throw new UsuarioNaoAutorizadoException();
        }

        Usuario usuario = usuarioRepository.findByEmail(atualizarUsuarioRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        usuario.setNome(atualizarUsuarioRequest.getNome());
        usuario.setTelefone(atualizarUsuarioRequest.getTelefone());

        if (atualizarUsuarioRequest.getUrlFoto() != null) {
            usuario.setUrlFoto(atualizarUsuarioRequest.getUrlFoto());
        }

        if (atualizarUsuarioRequest.getSenha() != null && !atualizarUsuarioRequest.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(atualizarUsuarioRequest.getSenha()));
        }

        return UsuarioOutputDto.fromEntity(usuarioRepository.save(usuario));
    }
}


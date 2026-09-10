package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.AtualizarUsuarioRequestDto;
import br.com.toquerendo.dto.input.CadastroUsuarioRequestDto;
import br.com.toquerendo.dto.output.UsuarioOutputDto;
import br.com.toquerendo.entity.Categoria;
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

    public UsuarioOutputDto editarUsuario(AtualizarUsuarioRequestDto req) {
        Usuario usuario = buscarUsuarioAutenticado();

        usuario.setNome(req.getNome() != null ? req.getNome() : usuario.getNome());
        usuario.setTelefone(req.getTelefone() != null ? req.getTelefone() : usuario.getTelefone());
        usuario.setCpf(req.getCpf() != null ? req.getCpf() : usuario.getCpf());
        usuario.setUrlFoto(req.getUrlFoto() != null ? req.getUrlFoto() : usuario.getUrlFoto());
        usuario.setPraiaId(req.getIdPraia() != null ? req.getIdPraia() : (usuario.getPraia() != null ? usuario.getPraia().getId() : null));

        return UsuarioOutputDto.fromEntity(usuarioRepository.save(usuario));
    }

    public UsuarioOutputDto consultarUsuarioAutenticado() {

        Usuario userEnt = buscarUsuarioAutenticado();
        UsuarioOutputDto user = UsuarioOutputDto.fromEntity(userEnt);
        Categoria cat = userEnt.getCategoria();
        user.setCategoria(userEnt.getCategoria() != null ? userEnt.getCategoria().getDescricao() : "Sem categoria definida");
        user.setPraiaAtual(userEnt.getPraia() != null ? userEnt.getPraia().getNome() : "Sem praia definida");
        return user;
    }

    private Usuario buscarUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new UsuarioNaoAutorizadoException();
        }

        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }
}


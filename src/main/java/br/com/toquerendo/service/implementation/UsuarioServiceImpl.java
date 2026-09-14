package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.AtualizarUsuarioRequestDto;
import br.com.toquerendo.dto.input.CadastroUsuarioRequestDto;
import br.com.toquerendo.dto.output.UsuarioOutputDto;
import br.com.toquerendo.entity.Categoria;
import br.com.toquerendo.entity.Praia;
import br.com.toquerendo.entity.Usuario;
import br.com.toquerendo.enums.CategoriaUsuarioEnum;
import br.com.toquerendo.exception.CpfJaCadastradoException;
import br.com.toquerendo.exception.EmailJaCadastradoException;
import br.com.toquerendo.exception.RuntimeApiException;
import br.com.toquerendo.exception.UsuarioNaoAutorizadoException;
import br.com.toquerendo.repository.PraiaRepository;
import br.com.toquerendo.repository.UsuarioRepository;
import br.com.toquerendo.utils.DocumentoUtils;
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

    private PraiaRepository praiaRepository;

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
        usuario.setPraiaId(cadastroRequest.getIdPraia());

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return UsuarioOutputDto.fromEntity(usuarioSalvo);
    }

    public UsuarioOutputDto editarUsuario(AtualizarUsuarioRequestDto req) {
        Usuario usuario = buscarUsuarioAutenticado();

        if(req.getIdPraia() != null){
            Praia praia = praiaRepository.findById(req.getIdPraia())
                    .orElseThrow(() -> new RuntimeApiException("Praia não encontrada."));
            usuario.setPraia(praia);
        }
        if(req.getCpf() != null){
            if (usuarioRepository.existsByCpf(req.getCpf()) && (usuario.getCpf() == null || !usuario.getCpf().equals(req.getCpf()))) {
                throw new CpfJaCadastradoException();
            }
            if(!DocumentoUtils.isCpfValido(req.getCpf())){
                throw new RuntimeApiException("CPF inválido.");
            }
            usuario.setCpf(req.getCpf());
        }
        usuario.setNome(req.getNome() != null ? req.getNome() : usuario.getNome());
        usuario.setTelefone(req.getTelefone() != null ? req.getTelefone() : usuario.getTelefone());
        usuario.setUrlFoto(req.getUrlFoto() != null ? req.getUrlFoto() : usuario.getUrlFoto());

        return UsuarioOutputDto.fromEntity(usuarioRepository.save(usuario));
    }

    public UsuarioOutputDto consultarUsuarioAutenticado() {
        Usuario userEnt = buscarUsuarioAutenticado();
        return UsuarioOutputDto.fromEntity(userEnt);
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


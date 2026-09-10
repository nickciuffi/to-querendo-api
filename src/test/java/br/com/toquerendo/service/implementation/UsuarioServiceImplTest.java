package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.output.UsuarioOutputDto;
import br.com.toquerendo.entity.Categoria;
import br.com.toquerendo.entity.Praia;
import br.com.toquerendo.entity.Usuario;
import br.com.toquerendo.exception.UsuarioNaoAutorizadoException;
import br.com.toquerendo.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    private static final String EMAIL_USUARIO_LOGADO = "usuario@email.com";

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @AfterEach
    void limparContextoDeSeguranca() {
        SecurityContextHolder.clearContext();
    }

    private void autenticarUsuario(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, List.of()));
    }

    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setEmail(EMAIL_USUARIO_LOGADO);
        usuario.setNome("Fulano de Tal");
        usuario.setContaAtiva(true);
        return usuario;
    }

    @Test
    void consultarUsuarioAutenticado_deveRetornarDadosDoUsuarioIdentificadoPeloEmailDoToken() {
        autenticarUsuario(EMAIL_USUARIO_LOGADO);

        Usuario usuario = criarUsuario();
        Categoria categoria = new Categoria();
        categoria.setDescricao("Vendedor");
        usuario.setCategoria(categoria);

        Praia praia = new Praia();
        praia.setNome("Praia do Forte");
        usuario.setPraia(praia);

        when(usuarioRepository.findByEmail(EMAIL_USUARIO_LOGADO)).thenReturn(Optional.of(usuario));

        UsuarioOutputDto output = usuarioService.consultarUsuarioAutenticado();

        assertThat(output.getEmail()).isEqualTo(EMAIL_USUARIO_LOGADO);
        assertThat(output.getNome()).isEqualTo("Fulano de Tal");
        assertThat(output.getCategoria()).isEqualTo("Vendedor");
        assertThat(output.getPraiaAtual()).isEqualTo("Praia do Forte");
    }

    @Test
    void consultarUsuarioAutenticado_quandoUsuarioSemCategoriaOuPraia_deveRetornarTextoPadrao() {
        autenticarUsuario(EMAIL_USUARIO_LOGADO);

        Usuario usuario = criarUsuario();
        when(usuarioRepository.findByEmail(EMAIL_USUARIO_LOGADO)).thenReturn(Optional.of(usuario));

        UsuarioOutputDto output = usuarioService.consultarUsuarioAutenticado();

        assertThat(output.getCategoria()).isEqualTo("Sem categoria definida");
        assertThat(output.getPraiaAtual()).isEqualTo("Sem praia definida");
    }

    @Test
    void consultarUsuarioAutenticado_quandoNaoHaUsuarioAutenticado_deveLancarUsuarioNaoAutorizadoException() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> usuarioService.consultarUsuarioAutenticado())
                .isInstanceOf(UsuarioNaoAutorizadoException.class);
    }

    @Test
    void consultarUsuarioAutenticado_quandoEmailDoTokenNaoCorrespondeAUsuarioCadastrado_deveLancarRuntimeException() {
        autenticarUsuario(EMAIL_USUARIO_LOGADO);
        when(usuarioRepository.findByEmail(EMAIL_USUARIO_LOGADO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.consultarUsuarioAutenticado())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado.");
    }

    @Test
    void consultarUsuarioAutenticado_devUsarSomenteOEmailDoTokenParaConsultar_naoAceitandoParametroExterno() {
        autenticarUsuario("outro-usuario@email.com");
        Usuario outroUsuario = new Usuario();
        outroUsuario.setEmail("outro-usuario@email.com");
        outroUsuario.setNome("Outro Usuário");
        when(usuarioRepository.findByEmail("outro-usuario@email.com")).thenReturn(Optional.of(outroUsuario));

        UsuarioOutputDto output = usuarioService.consultarUsuarioAutenticado();

        assertThat(output.getEmail()).isEqualTo("outro-usuario@email.com");
    }
}

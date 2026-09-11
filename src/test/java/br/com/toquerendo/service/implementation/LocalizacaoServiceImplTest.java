package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.AtualizarLocalizacaoInputDto;
import br.com.toquerendo.dto.output.LocalizacaoOutputDto;
import br.com.toquerendo.entity.Localizacao;
import br.com.toquerendo.entity.Usuario;
import br.com.toquerendo.repository.LocalizacaoRepository;
import br.com.toquerendo.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalizacaoServiceImplTest {

    private static final String EMAIL_USUARIO_LOGADO = "usuario@email.com";

    @Mock
    private LocalizacaoRepository localizacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private LocalizacaoServiceImpl localizacaoService;

    @BeforeEach
    void autenticarUsuario() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(EMAIL_USUARIO_LOGADO, null, List.of()));
    }

    @AfterEach
    void limparContextoDeSeguranca() {
        SecurityContextHolder.clearContext();
    }

    private Usuario criarUsuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setEmail(EMAIL_USUARIO_LOGADO);
        return usuario;
    }

    private AtualizarLocalizacaoInputDto criarInput() {
        AtualizarLocalizacaoInputDto input = new AtualizarLocalizacaoInputDto();
        input.setLatitude(BigDecimal.valueOf(-23.5505199));
        input.setLongitude(BigDecimal.valueOf(-46.6333094));
        return input;
    }

    @Test
    void atualizarLocalizacao_quandoUsuarioAindaNaoTemLocalizacao_deveCriarNova() {
        Usuario usuario = criarUsuario(1L);

        when(usuarioRepository.findByEmail(EMAIL_USUARIO_LOGADO)).thenReturn(Optional.of(usuario));
        when(localizacaoRepository.findById(1L)).thenReturn(Optional.empty());
        when(localizacaoRepository.save(any(Localizacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LocalizacaoOutputDto output = localizacaoService.atualizarLocalizacao(criarInput());

        ArgumentCaptor<Localizacao> captor = ArgumentCaptor.forClass(Localizacao.class);
        verify(localizacaoRepository).save(captor.capture());
        Localizacao salva = captor.getValue();

        assertThat(salva.getUsuario()).isEqualTo(usuario);
        assertThat(salva.getLatitude()).isEqualByComparingTo(BigDecimal.valueOf(-23.5505199));
        assertThat(salva.getLongitude()).isEqualByComparingTo(BigDecimal.valueOf(-46.6333094));

        assertThat(output.getLatitude()).isEqualByComparingTo(BigDecimal.valueOf(-23.5505199));
        assertThat(output.getLongitude()).isEqualByComparingTo(BigDecimal.valueOf(-46.6333094));
    }

    @Test
    void atualizarLocalizacao_quandoUsuarioJaTemLocalizacao_deveAtualizarExistente() {
        Usuario usuario = criarUsuario(1L);
        Localizacao localizacaoExistente = new Localizacao();
        localizacaoExistente.setUsuario(usuario);
        localizacaoExistente.setLatitude(BigDecimal.valueOf(-22.0));
        localizacaoExistente.setLongitude(BigDecimal.valueOf(-45.0));

        when(usuarioRepository.findByEmail(EMAIL_USUARIO_LOGADO)).thenReturn(Optional.of(usuario));
        when(localizacaoRepository.findById(1L)).thenReturn(Optional.of(localizacaoExistente));
        when(localizacaoRepository.save(any(Localizacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LocalizacaoOutputDto output = localizacaoService.atualizarLocalizacao(criarInput());

        assertThat(output.getLatitude()).isEqualByComparingTo(BigDecimal.valueOf(-23.5505199));
        assertThat(output.getLongitude()).isEqualByComparingTo(BigDecimal.valueOf(-46.6333094));
    }

    @Test
    void atualizarLocalizacao_quandoUsuarioLogadoNaoEncontrado_deveLancarRuntimeException() {
        when(usuarioRepository.findByEmail(EMAIL_USUARIO_LOGADO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> localizacaoService.atualizarLocalizacao(criarInput()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado.");
    }
}

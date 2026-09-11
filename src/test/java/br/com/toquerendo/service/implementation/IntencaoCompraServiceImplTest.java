package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.CriarIntencaoCompraInputDto;
import br.com.toquerendo.dto.output.IntencaoCompraOutputDto;
import br.com.toquerendo.entity.IntencaoCompra;
import br.com.toquerendo.entity.ProdutoBase;
import br.com.toquerendo.entity.Usuario;
import br.com.toquerendo.exception.ProdutoNaoEncontradoException;
import br.com.toquerendo.repository.IntencaoCompraRepository;
import br.com.toquerendo.repository.ProdutoBaseRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IntencaoCompraServiceImplTest {

    private static final String EMAIL_TURISTA_LOGADO = "turista@email.com";

    @Mock
    private IntencaoCompraRepository intencaoCompraRepository;

    @Mock
    private ProdutoBaseRepository produtoBaseRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private IntencaoCompraServiceImpl intencaoCompraService;

    @BeforeEach
    void autenticarTurista() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(EMAIL_TURISTA_LOGADO, null, List.of()));
    }

    @AfterEach
    void limparContextoDeSeguranca() {
        SecurityContextHolder.clearContext();
    }

    private Usuario criarUsuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setEmail(EMAIL_TURISTA_LOGADO);
        return usuario;
    }

    private ProdutoBase criarProdutoBase(Long id) {
        ProdutoBase produtoBase = new ProdutoBase();
        produtoBase.setId(id);
        produtoBase.setNome("Água de coco");
        produtoBase.setPrecoMinimo(BigDecimal.valueOf(5.0));
        produtoBase.setEstaAtivo(true);
        return produtoBase;
    }

    @Test
    void criarIntencaoCompra_deveAssociarAoUsuarioLogadoEAoProdutoBase() {
        Usuario usuario = criarUsuario(1L);
        ProdutoBase produtoBase = criarProdutoBase(10L);

        when(usuarioRepository.findByEmail(EMAIL_TURISTA_LOGADO)).thenReturn(Optional.of(usuario));
        when(produtoBaseRepository.findByIdAndEstaAtivoTrue(10L)).thenReturn(Optional.of(produtoBase));
        when(intencaoCompraRepository.save(any(IntencaoCompra.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CriarIntencaoCompraInputDto input = new CriarIntencaoCompraInputDto();
        input.setIdProdutoBase(10L);
        input.setDescricaoLocal("Guarda-sol azul, próximo ao quiosque 3");
        input.setObservacoes("Sem açúcar, por favor");

        IntencaoCompraOutputDto output = intencaoCompraService.criarIntencaoCompra(input);

        ArgumentCaptor<IntencaoCompra> captor = ArgumentCaptor.forClass(IntencaoCompra.class);
        verify(intencaoCompraRepository).save(captor.capture());
        IntencaoCompra salva = captor.getValue();

        assertThat(salva.getUsuario()).isEqualTo(usuario);
        assertThat(salva.getProdutoBase()).isEqualTo(produtoBase);
        assertThat(salva.getDescricaoLocal()).isEqualTo("Guarda-sol azul, próximo ao quiosque 3");
        assertThat(salva.getObservacoes()).isEqualTo("Sem açúcar, por favor");
        assertThat(salva.getTsCriacaoIntencao()).isNotNull();
        assertThat(salva.getTsConclusaoIntencao()).isNull();
        assertThat(salva.getEstaAtivo()).isTrue();

        assertThat(output.getIdUsuario()).isEqualTo(1L);
        assertThat(output.getIdProdutoBase()).isEqualTo(10L);
        assertThat(output.getNomeProdutoBase()).isEqualTo("Água de coco");
        assertThat(output.getTsCriacaoIntencao()).isNotNull();
        assertThat(output.getEstaAtivo()).isTrue();
    }

    @Test
    void criarIntencaoCompra_quandoProdutoBaseNaoEncontradoOuInativo_deveLancarProdutoNaoEncontradoException() {
        when(usuarioRepository.findByEmail(EMAIL_TURISTA_LOGADO)).thenReturn(Optional.of(criarUsuario(1L)));
        when(produtoBaseRepository.findByIdAndEstaAtivoTrue(10L)).thenReturn(Optional.empty());

        CriarIntencaoCompraInputDto input = new CriarIntencaoCompraInputDto();
        input.setIdProdutoBase(10L);

        assertThatThrownBy(() -> intencaoCompraService.criarIntencaoCompra(input))
                .isInstanceOf(ProdutoNaoEncontradoException.class);

        verify(intencaoCompraRepository, never()).save(any());
    }

    @Test
    void criarIntencaoCompra_quandoUsuarioLogadoNaoEncontrado_deveLancarRuntimeException() {
        when(usuarioRepository.findByEmail(EMAIL_TURISTA_LOGADO)).thenReturn(Optional.empty());

        CriarIntencaoCompraInputDto input = new CriarIntencaoCompraInputDto();
        input.setIdProdutoBase(10L);

        assertThatThrownBy(() -> intencaoCompraService.criarIntencaoCompra(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado.");

        verify(produtoBaseRepository, never()).findByIdAndEstaAtivoTrue(any());
    }
}

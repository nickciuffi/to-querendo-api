package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.CriarVendedorRequestDto;
import br.com.toquerendo.dto.output.UsuarioOutputDto;
import br.com.toquerendo.dto.output.VendedorLocalizacaoProdutosOutputDto;
import br.com.toquerendo.entity.Categoria;
import br.com.toquerendo.entity.Localizacao;
import br.com.toquerendo.entity.Praia;
import br.com.toquerendo.entity.ProdutoBase;
import br.com.toquerendo.entity.Usuario;
import br.com.toquerendo.entity.Vendedor;
import br.com.toquerendo.enums.CategoriaUsuarioEnum;
import br.com.toquerendo.exception.CriacaoVendedorException;
import br.com.toquerendo.exception.PraiaNaoEncontradaException;
import br.com.toquerendo.exception.ProdutoNaoEncontradoException;
import br.com.toquerendo.exception.VendedorJaCadastradoException;
import br.com.toquerendo.repository.LocalizacaoRepository;
import br.com.toquerendo.repository.PraiaRepository;
import br.com.toquerendo.repository.ProdutoBaseRepository;
import br.com.toquerendo.repository.ProdutoEspecificoRepository;
import br.com.toquerendo.repository.UsuarioRepository;
import br.com.toquerendo.repository.VendedorRepository;
import br.com.toquerendo.security.JwtService;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendedorServiceImplTest {

    private static final String EMAIL_USUARIO_LOGADO = "usuario@email.com";

    @Mock
    private VendedorRepository vendedorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProdutoEspecificoRepository produtoEspecificoRepository;

    @Mock
    private ProdutoBaseRepository produtoBaseRepository;

    @Mock
    private PraiaRepository praiaRepository;

    @Mock
    private LocalizacaoRepository localizacaoRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private VendedorServiceImpl vendedorService;

    @BeforeEach
    void autenticarUsuario() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(EMAIL_USUARIO_LOGADO, null, List.of()));
    }

    @AfterEach
    void limparContextoDeSeguranca() {
        SecurityContextHolder.clearContext();
    }

    private Usuario criarUsuarioTuristaApto() {
        Categoria categoriaTurista = new Categoria();
        categoriaTurista.setId(CategoriaUsuarioEnum.TURISTA.getId());

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail(EMAIL_USUARIO_LOGADO);
        usuario.setNome("João da Silva");
        usuario.setTelefone("11999999999");
        usuario.setCpf("12345678900");
        usuario.setContaAtiva(true);
        usuario.setCategoria(categoriaTurista);
        return usuario;
    }

    private Vendedor criarVendedor(Long id, Usuario usuario) {
        Vendedor vendedor = new Vendedor();
        vendedor.setId(id);
        vendedor.setUsuario(usuario);
        vendedor.setDescricao("Vendedor de água de coco");
        vendedor.setOnline(true);
        return vendedor;
    }

    // --- cadastrarVendedor ---

    @Test
    void cadastrarVendedor_comUsuarioApto_deveCriarVendedorEAtualizarCategoriaParaVendedor() {
        Usuario usuario = criarUsuarioTuristaApto();

        when(vendedorRepository.existsByUsuarioEmail(EMAIL_USUARIO_LOGADO)).thenReturn(false);
        when(usuarioRepository.findByEmailAndContaAtivaTrue(EMAIL_USUARIO_LOGADO)).thenReturn(Optional.of(usuario));
        when(vendedorRepository.save(any(Vendedor.class))).thenAnswer(invocation -> {
            Vendedor vendedor = invocation.getArgument(0);
            vendedor.setId(1L);
            return vendedor;
        });
        when(usuarioRepository.save(any())).thenAnswer(invocation -> {
            Usuario us = invocation.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });

        CriarVendedorRequestDto input = new CriarVendedorRequestDto();
        input.setDescricao("Vendedor de água de coco");

        UsuarioOutputDto output = vendedorService.cadastrarVendedor(input);

        ArgumentCaptor<Vendedor> vendedorCaptor = ArgumentCaptor.forClass(Vendedor.class);
        verify(vendedorRepository).save(vendedorCaptor.capture());
        assertThat(vendedorCaptor.getValue().getUsuario()).isEqualTo(usuario);
        assertThat(vendedorCaptor.getValue().getDescricao()).isEqualTo("Vendedor de água de coco");
        assertThat(vendedorCaptor.getValue().getOnline()).isFalse();

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        assertThat(usuarioCaptor.getValue().getCategoria().getId()).isEqualTo(CategoriaUsuarioEnum.VENDEDOR.getId());

        assertThat(output.getCategoria().getId()).isEqualTo(CategoriaUsuarioEnum.VENDEDOR.getId());
        assertThat(output.getEmail()).isEqualTo(EMAIL_USUARIO_LOGADO);
    }

    @Test
    void cadastrarVendedor_quandoJaCadastrado_deveLancarVendedorJaCadastradoException() {
        when(vendedorRepository.existsByUsuarioEmail(EMAIL_USUARIO_LOGADO)).thenReturn(true);

        CriarVendedorRequestDto input = new CriarVendedorRequestDto();

        assertThatThrownBy(() -> vendedorService.cadastrarVendedor(input))
                .isInstanceOf(VendedorJaCadastradoException.class);

        verify(usuarioRepository, never()).findByEmailAndContaAtivaTrue(any());
        verify(vendedorRepository, never()).save(any());
    }

    @Test
    void cadastrarVendedor_quandoUsuarioNaoEncontrado_deveLancarRuntimeException() {
        when(vendedorRepository.existsByUsuarioEmail(EMAIL_USUARIO_LOGADO)).thenReturn(false);
        when(usuarioRepository.findByEmailAndContaAtivaTrue(EMAIL_USUARIO_LOGADO)).thenReturn(Optional.empty());

        CriarVendedorRequestDto input = new CriarVendedorRequestDto();

        assertThatThrownBy(() -> vendedorService.cadastrarVendedor(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado.");

        verify(vendedorRepository, never()).save(any());
    }

    @Test
    void cadastrarVendedor_quandoUsuarioSemTelefone_deveLancarCriacaoVendedorException() {
        Usuario usuario = criarUsuarioTuristaApto();
        usuario.setTelefone(null);

        when(vendedorRepository.existsByUsuarioEmail(EMAIL_USUARIO_LOGADO)).thenReturn(false);
        when(usuarioRepository.findByEmailAndContaAtivaTrue(EMAIL_USUARIO_LOGADO)).thenReturn(Optional.of(usuario));

        CriarVendedorRequestDto input = new CriarVendedorRequestDto();

        assertThatThrownBy(() -> vendedorService.cadastrarVendedor(input))
                .isInstanceOf(CriacaoVendedorException.class)
                .hasMessageContaining("telefone e o CPF");

        verify(vendedorRepository, never()).save(any());
    }

    @Test
    void cadastrarVendedor_quandoUsuarioSemCpf_deveLancarCriacaoVendedorException() {
        Usuario usuario = criarUsuarioTuristaApto();
        usuario.setCpf(null);

        when(vendedorRepository.existsByUsuarioEmail(EMAIL_USUARIO_LOGADO)).thenReturn(false);
        when(usuarioRepository.findByEmailAndContaAtivaTrue(EMAIL_USUARIO_LOGADO)).thenReturn(Optional.of(usuario));

        CriarVendedorRequestDto input = new CriarVendedorRequestDto();

        assertThatThrownBy(() -> vendedorService.cadastrarVendedor(input))
                .isInstanceOf(CriacaoVendedorException.class)
                .hasMessageContaining("telefone e o CPF");

        verify(vendedorRepository, never()).save(any());
    }

    @Test
    void cadastrarVendedor_quandoContaNaoAtiva_deveLancarCriacaoVendedorException() {
        Usuario usuario = criarUsuarioTuristaApto();
        usuario.setContaAtiva(false);

        when(vendedorRepository.existsByUsuarioEmail(EMAIL_USUARIO_LOGADO)).thenReturn(false);
        when(usuarioRepository.findByEmailAndContaAtivaTrue(EMAIL_USUARIO_LOGADO)).thenReturn(Optional.of(usuario));

        CriarVendedorRequestDto input = new CriarVendedorRequestDto();

        assertThatThrownBy(() -> vendedorService.cadastrarVendedor(input))
                .isInstanceOf(CriacaoVendedorException.class)
                .hasMessageContaining("conta ativa");

        verify(vendedorRepository, never()).save(any());
    }

    @Test
    void cadastrarVendedor_quandoUsuarioNaoETurista_deveLancarCriacaoVendedorException() {
        Usuario usuario = criarUsuarioTuristaApto();
        Categoria categoriaVendedor = new Categoria();
        categoriaVendedor.setId(CategoriaUsuarioEnum.VENDEDOR.getId());
        usuario.setCategoria(categoriaVendedor);

        when(vendedorRepository.existsByUsuarioEmail(EMAIL_USUARIO_LOGADO)).thenReturn(false);
        when(usuarioRepository.findByEmailAndContaAtivaTrue(EMAIL_USUARIO_LOGADO)).thenReturn(Optional.of(usuario));

        CriarVendedorRequestDto input = new CriarVendedorRequestDto();

        assertThatThrownBy(() -> vendedorService.cadastrarVendedor(input))
                .isInstanceOf(CriacaoVendedorException.class)
                .hasMessageContaining("categoria de turista");

        verify(vendedorRepository, never()).save(any());
    }

    // --- consultarLocalizacaoVendedores ---

    @Test
    void consultarLocalizacaoVendedores_quandoPraiaNaoEncontrada_deveLancarPraiaNaoEncontradaException() {
        when(praiaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vendedorService.consultarLocalizacaoVendedores(null, 1L))
                .isInstanceOf(PraiaNaoEncontradaException.class);

        verify(vendedorRepository, never()).findAllVendedoresOnlinePorPraiaComAlgumProduto(any());
    }

    @Test
    void consultarLocalizacaoVendedores_semIdProdutoBase_deveConsultarPorPraia() {
        Praia praia = new Praia();
        praia.setId(1L);
        when(praiaRepository.findById(1L)).thenReturn(Optional.of(praia));

        Usuario usuario = criarUsuarioTuristaApto();
        Vendedor vendedor = criarVendedor(1L, usuario);

        when(vendedorRepository.findAllVendedoresOnlinePorPraiaComAlgumProduto(1L)).thenReturn(List.of(vendedor));
        when(localizacaoRepository.findById(1L)).thenReturn(Optional.empty());

        List<VendedorLocalizacaoProdutosOutputDto> resultado = vendedorService.consultarLocalizacaoVendedores(null, 1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getLatitude()).isEqualByComparingTo("0");
        assertThat(resultado.get(0).getLongitude()).isEqualByComparingTo("0");
        verify(produtoBaseRepository, never()).findByIdAndEstaAtivoTrue(any());
    }

    @Test
    void consultarLocalizacaoVendedores_comIdProdutoBaseValido_deveConsultarPorProdutoBaseEPraia() {
        Praia praia = new Praia();
        praia.setId(1L);
        when(praiaRepository.findById(1L)).thenReturn(Optional.of(praia));

        ProdutoBase produtoBase = new ProdutoBase();
        produtoBase.setId(10L);
        when(produtoBaseRepository.findByIdAndEstaAtivoTrue(10L)).thenReturn(Optional.of(produtoBase));

        Usuario usuario = criarUsuarioTuristaApto();
        Vendedor vendedor = criarVendedor(1L, usuario);
        Localizacao localizacao = new Localizacao();
        localizacao.setLatitude(new java.math.BigDecimal("-23.5614750"));
        localizacao.setLongitude(new java.math.BigDecimal("-46.6558830"));

        when(vendedorRepository.findAllVendedoresOnlinePorProdutoBaseEPraia(10L, 1L)).thenReturn(List.of(vendedor));
        when(localizacaoRepository.findById(1L)).thenReturn(Optional.of(localizacao));

        List<VendedorLocalizacaoProdutosOutputDto> resultado = vendedorService.consultarLocalizacaoVendedores(10L, 1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getLatitude()).isEqualByComparingTo("-23.5614750");
        verify(vendedorRepository, never()).findAllVendedoresOnlinePorPraiaComAlgumProduto(any());
    }

    @Test
    void consultarLocalizacaoVendedores_comIdProdutoBaseInexistenteOuInativo_deveLancarProdutoNaoEncontradoException() {
        Praia praia = new Praia();
        praia.setId(1L);
        when(praiaRepository.findById(1L)).thenReturn(Optional.of(praia));
        when(produtoBaseRepository.findByIdAndEstaAtivoTrue(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vendedorService.consultarLocalizacaoVendedores(10L, 1L))
                .isInstanceOf(ProdutoNaoEncontradoException.class);

        verify(vendedorRepository, never()).findAllVendedoresOnlinePorProdutoBaseEPraia(any(), any());
    }
}

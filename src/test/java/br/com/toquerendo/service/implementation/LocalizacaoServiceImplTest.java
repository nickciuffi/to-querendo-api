package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.AtualizarLocalizacaoInputDto;
import br.com.toquerendo.dto.output.LocalizacaoOutputDto;
import br.com.toquerendo.dto.output.VendedorLocalizacaoOutputDto;
import br.com.toquerendo.entity.Localizacao;
import br.com.toquerendo.entity.Praia;
import br.com.toquerendo.entity.ProdutoBase;
import br.com.toquerendo.entity.ProdutoEspecifico;
import br.com.toquerendo.entity.Usuario;
import br.com.toquerendo.entity.Vendedor;
import br.com.toquerendo.exception.PraiaNaoEncontradaException;
import br.com.toquerendo.exception.ProdutoNaoEncontradoException;
import br.com.toquerendo.repository.LocalizacaoRepository;
import br.com.toquerendo.repository.PraiaRepository;
import br.com.toquerendo.repository.ProdutoBaseRepository;
import br.com.toquerendo.repository.ProdutoEspecificoRepository;
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
class LocalizacaoServiceImplTest {

    private static final String EMAIL_USUARIO_LOGADO = "usuario@email.com";

    @Mock
    private LocalizacaoRepository localizacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProdutoBaseRepository produtoBaseRepository;

    @Mock
    private ProdutoEspecificoRepository produtoEspecificoRepository;

    @Mock
    private PraiaRepository praiaRepository;

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

    private Vendedor criarVendedor(Long id, Long idUsuario) {
        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);

        Vendedor vendedor = new Vendedor();
        vendedor.setId(id);
        vendedor.setUsuario(usuario);
        vendedor.setOnline(true);
        return vendedor;
    }

    private ProdutoEspecifico criarProdutoEspecifico(Long id, Vendedor vendedor) {
        ProdutoBase produtoBase = new ProdutoBase();
        produtoBase.setId(10L);

        ProdutoEspecifico produtoEspecifico = new ProdutoEspecifico();
        produtoEspecifico.setId(id);
        produtoEspecifico.setNome("Água de coco gelada");
        produtoEspecifico.setPreco(BigDecimal.valueOf(6.5));
        produtoEspecifico.setProdutoAtivo(true);
        produtoEspecifico.setProdutoBase(produtoBase);
        produtoEspecifico.setVendedor(vendedor);
        return produtoEspecifico;
    }

    private Localizacao criarLocalizacao() {
        Localizacao localizacao = new Localizacao();
        localizacao.setLatitude(BigDecimal.valueOf(-23.5505199));
        localizacao.setLongitude(BigDecimal.valueOf(-46.6333094));
        return localizacao;
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

    @Test
    void consultarLocalizacaoVendedores_quandoProdutoBaseNaoEncontradoOuInativo_deveLancarProdutoNaoEncontradoException() {
        when(produtoBaseRepository.findByIdAndEstaAtivoTrue(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> localizacaoService.consultarLocalizacaoVendedores(10L, 3L))
                .isInstanceOf(ProdutoNaoEncontradoException.class);

        verify(praiaRepository, never()).findById(any());
        verify(produtoEspecificoRepository, never()).findAllComVendedorOnlinePorProdutoBaseEPraia(any(), any());
    }

    @Test
    void consultarLocalizacaoVendedores_quandoPraiaNaoEncontrada_deveLancarPraiaNaoEncontradaException() {
        when(produtoBaseRepository.findByIdAndEstaAtivoTrue(10L)).thenReturn(Optional.of(new ProdutoBase()));
        when(praiaRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> localizacaoService.consultarLocalizacaoVendedores(10L, 3L))
                .isInstanceOf(PraiaNaoEncontradaException.class);

        verify(produtoEspecificoRepository, never()).findAllComVendedorOnlinePorProdutoBaseEPraia(any(), any());
    }

    @Test
    void consultarLocalizacaoVendedores_deveRetornarApenasVendedoresComLocalizacaoRegistrada() {
        Vendedor vendedorComLocalizacao = criarVendedor(1L, 100L);
        Vendedor vendedorSemLocalizacao = criarVendedor(2L, 200L);
        ProdutoEspecifico produtoDoVendedorComLocalizacao = criarProdutoEspecifico(5L, vendedorComLocalizacao);
        ProdutoEspecifico produtoDoVendedorSemLocalizacao = criarProdutoEspecifico(6L, vendedorSemLocalizacao);

        when(produtoBaseRepository.findByIdAndEstaAtivoTrue(10L)).thenReturn(Optional.of(new ProdutoBase()));
        when(praiaRepository.findById(3L)).thenReturn(Optional.of(new Praia()));
        when(produtoEspecificoRepository.findAllComVendedorOnlinePorProdutoBaseEPraia(10L, 3L))
                .thenReturn(List.of(produtoDoVendedorComLocalizacao, produtoDoVendedorSemLocalizacao));
        when(localizacaoRepository.findById(100L)).thenReturn(Optional.of(criarLocalizacao()));
        when(localizacaoRepository.findById(200L)).thenReturn(Optional.empty());

        List<VendedorLocalizacaoOutputDto> resultado = localizacaoService.consultarLocalizacaoVendedores(10L, 3L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdVendedor()).isEqualTo(1L);
        assertThat(resultado.get(0).getIdProdutoEspecifico()).isEqualTo(5L);
        assertThat(resultado.get(0).getPreco()).isEqualByComparingTo(BigDecimal.valueOf(6.5));
        assertThat(resultado.get(0).getLatitude()).isEqualByComparingTo(BigDecimal.valueOf(-23.5505199));
        assertThat(resultado.get(0).getLongitude()).isEqualByComparingTo(BigDecimal.valueOf(-46.6333094));
    }

    @Test
    void consultarLocalizacaoVendedores_quandoNenhumVendedorOnlineComProdutoAtivoNaPraia_deveRetornarListaVazia() {
        when(produtoBaseRepository.findByIdAndEstaAtivoTrue(10L)).thenReturn(Optional.of(new ProdutoBase()));
        when(praiaRepository.findById(3L)).thenReturn(Optional.of(new Praia()));
        when(produtoEspecificoRepository.findAllComVendedorOnlinePorProdutoBaseEPraia(10L, 3L)).thenReturn(List.of());

        List<VendedorLocalizacaoOutputDto> resultado = localizacaoService.consultarLocalizacaoVendedores(10L, 3L);

        assertThat(resultado).isEmpty();
        verify(localizacaoRepository, never()).findById(any());
    }
}

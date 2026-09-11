package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.CriarIntencaoCompraInputDto;
import br.com.toquerendo.dto.output.BanhistaComIntencoesOutputDto;
import br.com.toquerendo.dto.output.IntencaoCompraOutputDto;
import br.com.toquerendo.entity.IntencaoCompra;
import br.com.toquerendo.entity.Praia;
import br.com.toquerendo.entity.ProdutoBase;
import br.com.toquerendo.entity.ProdutoEspecifico;
import br.com.toquerendo.entity.Usuario;
import br.com.toquerendo.entity.Vendedor;
import br.com.toquerendo.enums.CategoriaUsuarioEnum;
import br.com.toquerendo.exception.ProdutoNaoEncontradoException;
import br.com.toquerendo.repository.IntencaoCompraRepository;
import br.com.toquerendo.repository.ProdutoBaseRepository;
import br.com.toquerendo.repository.ProdutoEspecificoRepository;
import br.com.toquerendo.repository.UsuarioRepository;
import br.com.toquerendo.repository.VendedorRepository;
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

    @Mock
    private ProdutoEspecificoRepository produtoEspecificoRepository;

    @Mock
    private VendedorRepository vendedorRepository;

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

    private Praia criarPraia(Long id) {
        Praia praia = new Praia();
        praia.setId(id);
        return praia;
    }

    private Vendedor criarVendedor(Long id, Praia praia) {
        Usuario usuario = new Usuario();
        usuario.setId(100L);
        usuario.setEmail(EMAIL_TURISTA_LOGADO);
        usuario.setPraia(praia);

        Vendedor vendedor = new Vendedor();
        vendedor.setId(id);
        vendedor.setUsuario(usuario);
        vendedor.setOnline(true);
        return vendedor;
    }

    private ProdutoEspecifico criarProdutoEspecifico(Vendedor vendedor, ProdutoBase produtoBase) {
        ProdutoEspecifico produtoEspecifico = new ProdutoEspecifico();
        produtoEspecifico.setId(1L);
        produtoEspecifico.setNome("Água de coco gelada");
        produtoEspecifico.setPreco(BigDecimal.valueOf(6.5));
        produtoEspecifico.setProdutoAtivo(true);
        produtoEspecifico.setVendedor(vendedor);
        produtoEspecifico.setProdutoBase(produtoBase);
        return produtoEspecifico;
    }

    private IntencaoCompra criarIntencaoDeCompra(Long id, Usuario banhista, ProdutoBase produtoBase) {
        IntencaoCompra intencaoCompra = new IntencaoCompra();
        intencaoCompra.setId(id);
        intencaoCompra.setUsuario(banhista);
        intencaoCompra.setProdutoBase(produtoBase);
        intencaoCompra.setEstaAtivo(true);
        return intencaoCompra;
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

    @Test
    void consultarBanhistas_comMultiplosBanhistas_deveAgruparIntencoesPorUsuarioMantendoOrdem() {
        Praia praia = criarPraia(3L);
        Vendedor vendedor = criarVendedor(1L, praia);
        ProdutoBase produtoBase10 = criarProdutoBase(10L);
        ProdutoBase produtoBase20 = criarProdutoBase(20L);

        Usuario banhistaA = criarUsuario(50L);
        banhistaA.setNome("Banhista A");
        Usuario banhistaB = criarUsuario(60L);
        banhistaB.setNome("Banhista B");

        IntencaoCompra intencaoA10 = criarIntencaoDeCompra(1L, banhistaA, produtoBase10);
        IntencaoCompra intencaoA20 = criarIntencaoDeCompra(2L, banhistaA, produtoBase20);
        IntencaoCompra intencaoB20 = criarIntencaoDeCompra(3L, banhistaB, produtoBase20);

        when(vendedorRepository.findByUsuarioEmail(EMAIL_TURISTA_LOGADO)).thenReturn(Optional.of(vendedor));
        when(produtoEspecificoRepository.findAllByVendedorIdAndProdutoAtivoTrue(1L))
                .thenReturn(List.of(
                        criarProdutoEspecifico(vendedor, produtoBase10),
                        criarProdutoEspecifico(vendedor, produtoBase20)));
        when(intencaoCompraRepository.findAllAtivasDeTuristasPorPraiaEProdutosBase(any(), any(), any()))
                .thenReturn(List.of(intencaoA10, intencaoA20, intencaoB20));

        List<BanhistaComIntencoesOutputDto> resultado = intencaoCompraService.consultarBanhistasComIntencaoDeCompra();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getIdUsuario()).isEqualTo(50L);
        assertThat(resultado.get(0).getIntencoesCompra()).hasSize(2);
        assertThat(resultado.get(1).getIdUsuario()).isEqualTo(60L);
        assertThat(resultado.get(1).getIntencoesCompra()).hasSize(1);
    }

    @Test
    void consultarBanhistas_quandoVendedorSemProdutosAtivos_deveRetornarListaVaziaSemConsultarIntencoes() {
        Vendedor vendedor = criarVendedor(1L, criarPraia(3L));

        when(vendedorRepository.findByUsuarioEmail(EMAIL_TURISTA_LOGADO)).thenReturn(Optional.of(vendedor));
        when(produtoEspecificoRepository.findAllByVendedorIdAndProdutoAtivoTrue(1L)).thenReturn(List.of());

        List<BanhistaComIntencoesOutputDto> resultado = intencaoCompraService.consultarBanhistasComIntencaoDeCompra();

        assertThat(resultado).isEmpty();
        verify(intencaoCompraRepository, never()).findAllAtivasDeTuristasPorPraiaEProdutosBase(any(), any(), any());
    }

    @Test
    void consultarBanhistas_quandoVendedorSemPraia_deveRetornarListaVaziaSemConsultarIntencoes() {
        Vendedor vendedor = criarVendedor(1L, null);
        ProdutoBase produtoBase = criarProdutoBase(10L);

        when(vendedorRepository.findByUsuarioEmail(EMAIL_TURISTA_LOGADO)).thenReturn(Optional.of(vendedor));
        when(produtoEspecificoRepository.findAllByVendedorIdAndProdutoAtivoTrue(1L))
                .thenReturn(List.of(criarProdutoEspecifico(vendedor, produtoBase)));

        List<BanhistaComIntencoesOutputDto> resultado = intencaoCompraService.consultarBanhistasComIntencaoDeCompra();

        assertThat(resultado).isEmpty();
        verify(intencaoCompraRepository, never()).findAllAtivasDeTuristasPorPraiaEProdutosBase(any(), any(), any());
    }

    @Test
    void consultarBanhistas_quandoRepositorioNaoRetornaIntencoes_deveRetornarListaVazia() {
        Vendedor vendedor = criarVendedor(1L, criarPraia(3L));
        ProdutoBase produtoBase = criarProdutoBase(10L);

        when(vendedorRepository.findByUsuarioEmail(EMAIL_TURISTA_LOGADO)).thenReturn(Optional.of(vendedor));
        when(produtoEspecificoRepository.findAllByVendedorIdAndProdutoAtivoTrue(1L))
                .thenReturn(List.of(criarProdutoEspecifico(vendedor, produtoBase)));
        when(intencaoCompraRepository.findAllAtivasDeTuristasPorPraiaEProdutosBase(any(), any(), any()))
                .thenReturn(List.of());

        List<BanhistaComIntencoesOutputDto> resultado = intencaoCompraService.consultarBanhistasComIntencaoDeCompra();

        assertThat(resultado).isEmpty();
    }

    @Test
    void consultarBanhistas_devePassarIdsDeProdutoBaseDistintosECategoriaTuristaEIdPraiaCorretos() {
        Praia praia = criarPraia(3L);
        Vendedor vendedor = criarVendedor(1L, praia);
        ProdutoBase produtoBase = criarProdutoBase(10L);

        when(vendedorRepository.findByUsuarioEmail(EMAIL_TURISTA_LOGADO)).thenReturn(Optional.of(vendedor));
        when(produtoEspecificoRepository.findAllByVendedorIdAndProdutoAtivoTrue(1L))
                .thenReturn(List.of(
                        criarProdutoEspecifico(vendedor, produtoBase),
                        criarProdutoEspecifico(vendedor, produtoBase)));
        when(intencaoCompraRepository.findAllAtivasDeTuristasPorPraiaEProdutosBase(any(), any(), any()))
                .thenReturn(List.of());

        intencaoCompraService.consultarBanhistasComIntencaoDeCompra();

        ArgumentCaptor<Integer> categoriaCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Long> praiaCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<List<Long>> produtosBaseCaptor = ArgumentCaptor.forClass(List.class);

        verify(intencaoCompraRepository).findAllAtivasDeTuristasPorPraiaEProdutosBase(
                categoriaCaptor.capture(), praiaCaptor.capture(), produtosBaseCaptor.capture());

        assertThat(categoriaCaptor.getValue()).isEqualTo(CategoriaUsuarioEnum.TURISTA.getId());
        assertThat(praiaCaptor.getValue()).isEqualTo(3L);
        assertThat(produtosBaseCaptor.getValue()).containsExactly(10L);
    }

    @Test
    void consultarBanhistas_quandoVendedorNaoEncontrado_deveLancarRuntimeException() {
        when(vendedorRepository.findByUsuarioEmail(EMAIL_TURISTA_LOGADO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> intencaoCompraService.consultarBanhistasComIntencaoDeCompra())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Vendedor não encontrado.");

        verify(produtoEspecificoRepository, never()).findAllByVendedorIdAndProdutoAtivoTrue(any());
    }
}

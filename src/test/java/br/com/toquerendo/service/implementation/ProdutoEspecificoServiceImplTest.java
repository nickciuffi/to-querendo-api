package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.AtualizarProdutoEspecificoInputDto;
import br.com.toquerendo.dto.input.CriarProdutoEspecificoInputDto;
import br.com.toquerendo.dto.output.ProdutoEspecificoOutputDto;
import br.com.toquerendo.entity.ProdutoBase;
import br.com.toquerendo.entity.ProdutoEspecifico;
import br.com.toquerendo.entity.Usuario;
import br.com.toquerendo.entity.Vendedor;
import br.com.toquerendo.exception.ProdutoNaoEncontradoException;
import br.com.toquerendo.exception.ProdutoNaoPertenceAoVendedorException;
import br.com.toquerendo.exception.RuntimeApiException;
import br.com.toquerendo.repository.ProdutoBaseRepository;
import br.com.toquerendo.repository.ProdutoEspecificoRepository;
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
class ProdutoEspecificoServiceImplTest {

    private static final String EMAIL_VENDEDOR_LOGADO = "vendedor@email.com";

    @Mock
    private ProdutoEspecificoRepository produtoEspecificoRepository;

    @Mock
    private ProdutoBaseRepository produtoBaseRepository;

    @Mock
    private VendedorRepository vendedorRepository;

    @InjectMocks
    private ProdutoEspecificoServiceImpl produtoEspecificoService;

    @BeforeEach
    void autenticarVendedor() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(EMAIL_VENDEDOR_LOGADO, null, List.of()));
    }

    @AfterEach
    void limparContextoDeSeguranca() {
        SecurityContextHolder.clearContext();
    }

    private Vendedor criarVendedor(Long id) {
        Usuario usuario = new Usuario();
        usuario.setEmail(EMAIL_VENDEDOR_LOGADO);

        Vendedor vendedor = new Vendedor();
        vendedor.setId(id);
        vendedor.setUsuario(usuario);
        vendedor.setOnline(true);
        return vendedor;
    }

    private ProdutoBase criarProdutoBase(BigDecimal precoMinimo) {
        ProdutoBase produtoBase = new ProdutoBase();
        produtoBase.setId(10L);
        produtoBase.setNome("Água de coco");
        produtoBase.setPrecoMinimo(precoMinimo);
        produtoBase.setEstaAtivo(true);
        return produtoBase;
    }

    private ProdutoEspecifico criarProdutoEspecificoEntity(Vendedor vendedor, ProdutoBase produtoBase) {
        ProdutoEspecifico produtoEspecifico = new ProdutoEspecifico();
        produtoEspecifico.setId(1L);
        produtoEspecifico.setNome("Água de coco gelada");
        produtoEspecifico.setPreco(BigDecimal.valueOf(6.5));
        produtoEspecifico.setProdutoAtivo(true);
        produtoEspecifico.setVendedor(vendedor);
        produtoEspecifico.setProdutoBase(produtoBase);
        return produtoEspecifico;
    }

    @Test
    void criarProdutoEspecifico_deveAssociarAoVendedorLogadoEAoProdutoBase() {
        Vendedor vendedor = criarVendedor(1L);
        ProdutoBase produtoBase = criarProdutoBase(BigDecimal.valueOf(5.0));

        when(vendedorRepository.findByUsuarioEmail(EMAIL_VENDEDOR_LOGADO)).thenReturn(Optional.of(vendedor));
        when(produtoBaseRepository.findByIdAndEstaAtivoTrue(10L)).thenReturn(Optional.of(produtoBase));
        when(produtoEspecificoRepository.save(any(ProdutoEspecifico.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CriarProdutoEspecificoInputDto input = new CriarProdutoEspecificoInputDto();
        input.setNome("Água de coco gelada");
        input.setIdProdutoBase(10L);
        input.setPreco(BigDecimal.valueOf(6.5));

        ProdutoEspecificoOutputDto output = produtoEspecificoService.criarProdutoEspecifico(input);

        ArgumentCaptor<ProdutoEspecifico> captor = ArgumentCaptor.forClass(ProdutoEspecifico.class);
        verify(produtoEspecificoRepository).save(captor.capture());
        ProdutoEspecifico salvo = captor.getValue();

        assertThat(salvo.getVendedor()).isEqualTo(vendedor);
        assertThat(salvo.getProdutoBase()).isEqualTo(produtoBase);
        assertThat(salvo.getProdutoAtivo()).isTrue();
        assertThat(salvo.getTsCriacaoProduto()).isNotNull();

        assertThat(output.getIdVendedor()).isEqualTo(1L);
        assertThat(output.getIdProdutoBase()).isEqualTo(10L);
        assertThat(output.getProdutoAtivo()).isTrue();
    }

    @Test
    void criarProdutoEspecifico_quandoProdutoBaseNaoEncontradoOuInativo_deveLancarProdutoNaoEncontradoException() {
        when(vendedorRepository.findByUsuarioEmail(EMAIL_VENDEDOR_LOGADO)).thenReturn(Optional.of(criarVendedor(1L)));
        when(produtoBaseRepository.findByIdAndEstaAtivoTrue(10L)).thenReturn(Optional.empty());

        CriarProdutoEspecificoInputDto input = new CriarProdutoEspecificoInputDto();
        input.setNome("Água de coco gelada");
        input.setIdProdutoBase(10L);
        input.setPreco(BigDecimal.valueOf(6.5));

        assertThatThrownBy(() -> produtoEspecificoService.criarProdutoEspecifico(input))
                .isInstanceOf(ProdutoNaoEncontradoException.class);

        verify(produtoEspecificoRepository, never()).save(any());
    }

    @Test
    void criarProdutoEspecifico_quandoPrecoMenorQueOMinimoDoProdutoBase_deveLancarRuntimeApiException() {
        when(vendedorRepository.findByUsuarioEmail(EMAIL_VENDEDOR_LOGADO)).thenReturn(Optional.of(criarVendedor(1L)));
        when(produtoBaseRepository.findByIdAndEstaAtivoTrue(10L))
                .thenReturn(Optional.of(criarProdutoBase(BigDecimal.valueOf(10.0))));

        CriarProdutoEspecificoInputDto input = new CriarProdutoEspecificoInputDto();
        input.setNome("Água de coco gelada");
        input.setIdProdutoBase(10L);
        input.setPreco(BigDecimal.valueOf(5.0));

        assertThatThrownBy(() -> produtoEspecificoService.criarProdutoEspecifico(input))
                .isInstanceOf(RuntimeApiException.class)
                .hasMessageContaining("preço mínimo");

        verify(produtoEspecificoRepository, never()).save(any());
    }

    @Test
    void criarProdutoEspecifico_quandoVendedorLogadoNaoEncontrado_deveLancarRuntimeException() {
        when(vendedorRepository.findByUsuarioEmail(EMAIL_VENDEDOR_LOGADO)).thenReturn(Optional.empty());

        CriarProdutoEspecificoInputDto input = new CriarProdutoEspecificoInputDto();
        input.setNome("Água de coco gelada");
        input.setIdProdutoBase(10L);
        input.setPreco(BigDecimal.valueOf(6.5));

        assertThatThrownBy(() -> produtoEspecificoService.criarProdutoEspecifico(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Vendedor não encontrado.");

        verify(produtoBaseRepository, never()).findByIdAndEstaAtivoTrue(any());
    }

    @Test
    void atualizarProdutoEspecifico_quandoNaoExiste_deveLancarProdutoNaoEncontradoException() {
        when(vendedorRepository.findByUsuarioEmail(EMAIL_VENDEDOR_LOGADO)).thenReturn(Optional.of(criarVendedor(1L)));
        when(produtoEspecificoRepository.findById(99L)).thenReturn(Optional.empty());

        AtualizarProdutoEspecificoInputDto input = new AtualizarProdutoEspecificoInputDto();

        assertThatThrownBy(() -> produtoEspecificoService.atualizarProdutoEspecifico(99L, input))
                .isInstanceOf(ProdutoNaoEncontradoException.class);
    }

    @Test
    void atualizarProdutoEspecifico_quandoVendedorLogadoNaoEhDono_deveLancarProdutoNaoPertenceAoVendedorException() {
        Vendedor vendedorLogado = criarVendedor(1L);
        Vendedor outroVendedor = criarVendedor(2L);
        ProdutoBase produtoBase = criarProdutoBase(BigDecimal.valueOf(5.0));
        ProdutoEspecifico produtoDeOutroVendedor = criarProdutoEspecificoEntity(outroVendedor, produtoBase);

        when(vendedorRepository.findByUsuarioEmail(EMAIL_VENDEDOR_LOGADO)).thenReturn(Optional.of(vendedorLogado));
        when(produtoEspecificoRepository.findById(1L)).thenReturn(Optional.of(produtoDeOutroVendedor));

        AtualizarProdutoEspecificoInputDto input = new AtualizarProdutoEspecificoInputDto();
        input.setNome("Tentativa de alteração indevida");

        assertThatThrownBy(() -> produtoEspecificoService.atualizarProdutoEspecifico(1L, input))
                .isInstanceOf(ProdutoNaoPertenceAoVendedorException.class);

        verify(produtoEspecificoRepository, never()).save(any());
    }

    @Test
    void atualizarProdutoEspecifico_quandoPrecoMenorQueOMinimoDoProdutoBase_deveLancarRuntimeApiException() {
        Vendedor vendedor = criarVendedor(1L);
        ProdutoBase produtoBase = criarProdutoBase(BigDecimal.valueOf(10.0));
        ProdutoEspecifico produtoExistente = criarProdutoEspecificoEntity(vendedor, produtoBase);

        when(vendedorRepository.findByUsuarioEmail(EMAIL_VENDEDOR_LOGADO)).thenReturn(Optional.of(vendedor));
        when(produtoEspecificoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));

        AtualizarProdutoEspecificoInputDto input = new AtualizarProdutoEspecificoInputDto();
        input.setPreco(BigDecimal.valueOf(5.0));

        assertThatThrownBy(() -> produtoEspecificoService.atualizarProdutoEspecifico(1L, input))
                .isInstanceOf(RuntimeApiException.class)
                .hasMessageContaining("preço mínimo");

        verify(produtoEspecificoRepository, never()).save(any());
    }

    @Test
    void atualizarProdutoEspecifico_quandoDonoDoProdutoEDadosValidos_deveAtualizarSomenteCamposInformados() {
        Vendedor vendedor = criarVendedor(1L);
        ProdutoBase produtoBase = criarProdutoBase(BigDecimal.valueOf(5.0));
        ProdutoEspecifico produtoExistente = criarProdutoEspecificoEntity(vendedor, produtoBase);
        produtoExistente.setDescricao("Descrição original");

        when(vendedorRepository.findByUsuarioEmail(EMAIL_VENDEDOR_LOGADO)).thenReturn(Optional.of(vendedor));
        when(produtoEspecificoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));
        when(produtoEspecificoRepository.save(any(ProdutoEspecifico.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AtualizarProdutoEspecificoInputDto input = new AtualizarProdutoEspecificoInputDto();
        input.setProdutoAtivo(false);
        // nome, descricao, urlFoto e preco ficam null propositalmente (atualização parcial)

        ProdutoEspecificoOutputDto output = produtoEspecificoService.atualizarProdutoEspecifico(1L, input);

        assertThat(output.getProdutoAtivo()).isFalse();
        assertThat(output.getNome()).isEqualTo("Água de coco gelada");
        assertThat(output.getDescricao()).isEqualTo("Descrição original");
        assertThat(output.getPreco()).isEqualByComparingTo(BigDecimal.valueOf(6.5));
    }

    @Test
    void atualizarProdutoEspecifico_semAlterarPreco_naoDeveLancarExcecaoDeValidacaoDePreco() {
        Vendedor vendedor = criarVendedor(1L);
        ProdutoBase produtoBase = criarProdutoBase(BigDecimal.valueOf(10.0));
        ProdutoEspecifico produtoExistente = criarProdutoEspecificoEntity(vendedor, produtoBase);

        when(vendedorRepository.findByUsuarioEmail(EMAIL_VENDEDOR_LOGADO)).thenReturn(Optional.of(vendedor));
        when(produtoEspecificoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));
        when(produtoEspecificoRepository.save(any(ProdutoEspecifico.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AtualizarProdutoEspecificoInputDto input = new AtualizarProdutoEspecificoInputDto();
        input.setNome("Novo nome, sem mexer no preço");

        ProdutoEspecificoOutputDto output = produtoEspecificoService.atualizarProdutoEspecifico(1L, input);

        assertThat(output.getNome()).isEqualTo("Novo nome, sem mexer no preço");
        assertThat(output.getPreco()).isEqualByComparingTo(BigDecimal.valueOf(6.5));
    }

    @Test
    void consultarProdutosAtivosDoVendedorLogado_deveRetornarApenasProdutosAtivosDoVendedor() {
        Vendedor vendedor = criarVendedor(1L);
        ProdutoBase produtoBase = criarProdutoBase(BigDecimal.valueOf(5.0));
        ProdutoEspecifico produtoAtivo = criarProdutoEspecificoEntity(vendedor, produtoBase);

        when(vendedorRepository.findByUsuarioEmail(EMAIL_VENDEDOR_LOGADO)).thenReturn(Optional.of(vendedor));
        when(produtoEspecificoRepository.findAllByVendedorIdAndProdutoAtivoTrue(1L))
                .thenReturn(List.of(produtoAtivo));

        List<ProdutoEspecificoOutputDto> resultado = produtoEspecificoService.consultarProdutosAtivosDoVendedorLogado();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdVendedor()).isEqualTo(1L);
        assertThat(resultado.get(0).getProdutoAtivo()).isTrue();
    }

    @Test
    void consultarProdutosAtivosDoVendedorLogado_quandoNaoHaProdutosAtivos_deveRetornarListaVazia() {
        Vendedor vendedor = criarVendedor(1L);
        when(vendedorRepository.findByUsuarioEmail(EMAIL_VENDEDOR_LOGADO)).thenReturn(Optional.of(vendedor));
        when(produtoEspecificoRepository.findAllByVendedorIdAndProdutoAtivoTrue(1L)).thenReturn(List.of());

        List<ProdutoEspecificoOutputDto> resultado = produtoEspecificoService.consultarProdutosAtivosDoVendedorLogado();

        assertThat(resultado).isEmpty();
    }
}

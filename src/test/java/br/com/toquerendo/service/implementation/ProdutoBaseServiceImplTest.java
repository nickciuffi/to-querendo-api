package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.AtualizarProdutoBaseInputDto;
import br.com.toquerendo.dto.input.CriarProdutoBaseInputDto;
import br.com.toquerendo.dto.output.ProdutoBaseOutputDto;
import br.com.toquerendo.entity.ProdutoBase;
import br.com.toquerendo.exception.ProdutoNaoEncontradoException;
import br.com.toquerendo.repository.ProdutoBaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class ProdutoBaseServiceImplTest {

    @Mock
    private ProdutoBaseRepository produtoBaseRepository;

    @InjectMocks
    private ProdutoBaseServiceImpl produtoBaseService;

    private ProdutoBase criarProdutoBase(Long id) {
        ProdutoBase produtoBase = new ProdutoBase();
        produtoBase.setId(id);
        produtoBase.setNome("Água de coco");
        produtoBase.setDescricao("Água de coco gelada");
        produtoBase.setUrlFoto("https://exemplo.com/agua-de-coco.jpg");
        produtoBase.setPrecoMinimo(BigDecimal.valueOf(5.0));
        produtoBase.setEstaAtivo(true);
        return produtoBase;
    }

    @Test
    void criarProdutoBase_deveSalvarComEstaAtivoTrueERetornarOutput() {
        CriarProdutoBaseInputDto input = new CriarProdutoBaseInputDto();
        input.setNome("Água de coco");
        input.setDescricao("Água de coco gelada");
        input.setUrlFoto("https://exemplo.com/agua-de-coco.jpg");
        input.setPrecoMinimo(BigDecimal.valueOf(5.0));

        when(produtoBaseRepository.save(any(ProdutoBase.class)))
                .thenAnswer(invocation -> {
                    ProdutoBase produtoBase = invocation.getArgument(0);
                    produtoBase.setId(1L);
                    return produtoBase;
                });

        ProdutoBaseOutputDto output = produtoBaseService.criarProdutoBase(input);

        ArgumentCaptor<ProdutoBase> captor = ArgumentCaptor.forClass(ProdutoBase.class);
        verify(produtoBaseRepository).save(captor.capture());
        ProdutoBase salvo = captor.getValue();

        assertThat(salvo.getNome()).isEqualTo("Água de coco");
        assertThat(salvo.getDescricao()).isEqualTo("Água de coco gelada");
        assertThat(salvo.getPrecoMinimo()).isEqualByComparingTo(BigDecimal.valueOf(5.0));
        assertThat(salvo.getEstaAtivo()).isTrue();

        assertThat(output.getId()).isEqualTo(1L);
        assertThat(output.getNome()).isEqualTo("Água de coco");
        assertThat(output.getEstaAtivo()).isTrue();
    }

    @Test
    void consultarProdutosBaseAtivos_comIdPraiaInformado_deveFiltrarPorPraia() {
        when(produtoBaseRepository.findAllByEstaAtivoTrueAndIdPraia(3))
                .thenReturn(List.of(criarProdutoBase(1L)));

        List<ProdutoBaseOutputDto> resultado = produtoBaseService.consultarProdutosBaseAtivos(3);

        assertThat(resultado).hasSize(1);
        verify(produtoBaseRepository).findAllByEstaAtivoTrueAndIdPraia(3);
        verify(produtoBaseRepository, never()).findAllByEstaAtivoTrue();
    }

    @Test
    void consultarProdutosBaseAtivos_semIdPraia_deveConsultarTodosOsAtivos() {
        when(produtoBaseRepository.findAllByEstaAtivoTrue())
                .thenReturn(List.of(criarProdutoBase(1L), criarProdutoBase(2L)));

        List<ProdutoBaseOutputDto> resultado = produtoBaseService.consultarProdutosBaseAtivos(null);

        assertThat(resultado).hasSize(2);
        verify(produtoBaseRepository).findAllByEstaAtivoTrue();
        verify(produtoBaseRepository, never()).findAllByEstaAtivoTrueAndIdPraia(any());
    }

    @Test
    void consultarProdutosBase_deveRetornarTodosOsProdutosIndependenteDoStatus() {
        when(produtoBaseRepository.findAll())
                .thenReturn(List.of(criarProdutoBase(1L), criarProdutoBase(2L)));

        List<ProdutoBaseOutputDto> resultado = produtoBaseService.consultarProdutosBase();

        assertThat(resultado).hasSize(2);
    }

    @Test
    void deletarProdutoBase_quandoEncontrado_deveDeletar() {
        ProdutoBase produtoBase = criarProdutoBase(1L);
        when(produtoBaseRepository.findById(1L)).thenReturn(Optional.of(produtoBase));

        produtoBaseService.deletarProdutoBase(1L);

        verify(produtoBaseRepository).delete(produtoBase);
    }

    @Test
    void deletarProdutoBase_quandoNaoEncontrado_deveLancarProdutoNaoEncontradoException() {
        when(produtoBaseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produtoBaseService.deletarProdutoBase(1L))
                .isInstanceOf(ProdutoNaoEncontradoException.class);

        verify(produtoBaseRepository, never()).delete(any());
    }

    @Test
    void atualizarProdutoBase_comTodosOsCamposInformados_deveSubstituirTodosOsValores() {
        ProdutoBase existente = criarProdutoBase(1L);
        when(produtoBaseRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(produtoBaseRepository.save(any(ProdutoBase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AtualizarProdutoBaseInputDto input = new AtualizarProdutoBaseInputDto();
        input.setNome("Água de coco gelada");
        input.setDescricao("Nova descrição");
        input.setUrlFoto("https://exemplo.com/nova-foto.jpg");
        input.setPrecoMinimo(BigDecimal.valueOf(7.5));
        input.setEstaAtivo(false);

        ProdutoBaseOutputDto output = produtoBaseService.atualizarProdutoBase(1L, input);

        assertThat(output.getNome()).isEqualTo("Água de coco gelada");
        assertThat(output.getDescricao()).isEqualTo("Nova descrição");
        assertThat(output.getUrlFoto()).isEqualTo("https://exemplo.com/nova-foto.jpg");
        assertThat(new BigDecimal(output.getPrecoMinimo())).isEqualByComparingTo(BigDecimal.valueOf(7.5));
        assertThat(output.getEstaAtivo()).isFalse();
    }

    @Test
    void atualizarProdutoBase_comCamposNulos_deveManterValoresExistentes() {
        ProdutoBase existente = criarProdutoBase(1L);
        when(produtoBaseRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(produtoBaseRepository.save(any(ProdutoBase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AtualizarProdutoBaseInputDto input = new AtualizarProdutoBaseInputDto();

        ProdutoBaseOutputDto output = produtoBaseService.atualizarProdutoBase(1L, input);

        assertThat(output.getNome()).isEqualTo("Água de coco");
        assertThat(output.getDescricao()).isEqualTo("Água de coco gelada");
        assertThat(output.getUrlFoto()).isEqualTo("https://exemplo.com/agua-de-coco.jpg");
        assertThat(new BigDecimal(output.getPrecoMinimo())).isEqualByComparingTo(BigDecimal.valueOf(5.0));
        assertThat(output.getEstaAtivo()).isTrue();
    }

    @Test
    void atualizarProdutoBase_quandoNaoEncontrado_deveLancarProdutoNaoEncontradoException() {
        when(produtoBaseRepository.findById(1L)).thenReturn(Optional.empty());

        AtualizarProdutoBaseInputDto input = new AtualizarProdutoBaseInputDto();

        assertThatThrownBy(() -> produtoBaseService.atualizarProdutoBase(1L, input))
                .isInstanceOf(ProdutoNaoEncontradoException.class);

        verify(produtoBaseRepository, never()).save(any());
    }
}

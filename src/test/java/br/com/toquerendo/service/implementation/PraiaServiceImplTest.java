package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.AtualizarPraiaInputDto;
import br.com.toquerendo.dto.input.CriarPraiaInputDto;
import br.com.toquerendo.dto.output.PraiaOutputDto;
import br.com.toquerendo.entity.Praia;
import br.com.toquerendo.exception.PraiaNaoEncontradaException;
import br.com.toquerendo.repository.PraiaRepository;
import br.com.toquerendo.repository.VendedorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PraiaServiceImplTest {

    @Mock
    private PraiaRepository praiaRepository;

    @Mock
    private VendedorRepository vendedorRepository;

    @InjectMocks
    private PraiaServiceImpl praiaService;

    private Praia criarPraiaEntity() {
        Praia praia = new Praia();
        praia.setId(1L);
        praia.setNome("Praia de Boa Viagem");
        praia.setUrlFoto("https://exemplo.com/boa-viagem.jpg");
        praia.setCidade("Recife");
        praia.setEstado("PE");
        return praia;
    }

    @Test
    void criarPraia_devePersistirEDevolverPraiaCriada() {
        CriarPraiaInputDto input = new CriarPraiaInputDto();
        input.setNome("Praia de Boa Viagem");
        input.setUrlFoto("https://exemplo.com/boa-viagem.jpg");
        input.setCidade("Recife");
        input.setEstado("PE");

        Praia praiaSalva = criarPraiaEntity();
        when(praiaRepository.save(any(Praia.class))).thenReturn(praiaSalva);

        PraiaOutputDto output = praiaService.criarPraia(input);

        ArgumentCaptor<Praia> captor = ArgumentCaptor.forClass(Praia.class);
        verify(praiaRepository).save(captor.capture());
        Praia praiaEnviadaParaSalvar = captor.getValue();
        assertThat(praiaEnviadaParaSalvar.getNome()).isEqualTo("Praia de Boa Viagem");
        assertThat(praiaEnviadaParaSalvar.getCidade()).isEqualTo("Recife");
        assertThat(praiaEnviadaParaSalvar.getEstado()).isEqualTo("PE");

        assertThat(output.getId()).isEqualTo(1L);
        assertThat(output.getNome()).isEqualTo("Praia de Boa Viagem");
        assertThat(output.getCidade()).isEqualTo("Recife");
        assertThat(output.getEstado()).isEqualTo("PE");
    }

    @Test
    void consultarPraias_deveRetornarTodasAsPraiasCadastradas() {
        when(praiaRepository.findAll()).thenReturn(List.of(criarPraiaEntity()));

        List<PraiaOutputDto> resultado = praiaService.consultarPraias();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Praia de Boa Viagem");
    }

    @Test
    void consultarPraias_quandoNaoHaPraias_deveRetornarListaVazia() {
        when(praiaRepository.findAll()).thenReturn(List.of());

        List<PraiaOutputDto> resultado = praiaService.consultarPraias();

        assertThat(resultado).isEmpty();
    }

    @Test
    void consultarPraiaPorId_quandoExiste_deveRetornarComQtdVendedoresOnline() {
        when(praiaRepository.findById(1L)).thenReturn(Optional.of(criarPraiaEntity()));
        when(vendedorRepository.countVendedoresPorPraia(1L)).thenReturn(5);

        PraiaOutputDto output = praiaService.consultarPraiaPorId(1L);

        assertThat(output.getId()).isEqualTo(1L);
        assertThat(output.getQtdVendedoresOnline()).isEqualTo(5);
    }

    @Test
    void consultarPraiaPorId_quandoNaoExiste_deveLancarPraiaNaoEncontradaException() {
        when(praiaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> praiaService.consultarPraiaPorId(99L))
                .isInstanceOf(PraiaNaoEncontradaException.class);

        verify(vendedorRepository, never()).countVendedoresPorPraia(anyLong());
    }

    @Test
    void atualizarPraia_deveAtualizarSomenteOsCamposInformados() {
        Praia praiaExistente = criarPraiaEntity();
        when(praiaRepository.findById(1L)).thenReturn(Optional.of(praiaExistente));
        when(praiaRepository.save(any(Praia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AtualizarPraiaInputDto input = new AtualizarPraiaInputDto();
        input.setNome("Novo nome da praia");
        // demais campos ficam null propositalmente, simulando atualização parcial

        PraiaOutputDto output = praiaService.atualizarPraia(1L, input);

        assertThat(output.getNome()).isEqualTo("Novo nome da praia");
        assertThat(output.getCidade()).isEqualTo("Recife");
        assertThat(output.getEstado()).isEqualTo("PE");
        assertThat(output.getUrlFoto()).isEqualTo("https://exemplo.com/boa-viagem.jpg");
    }

    @Test
    void atualizarPraia_quandoNaoExiste_deveLancarPraiaNaoEncontradaException() {
        when(praiaRepository.findById(99L)).thenReturn(Optional.empty());

        AtualizarPraiaInputDto input = new AtualizarPraiaInputDto();
        input.setNome("Não importa");

        assertThatThrownBy(() -> praiaService.atualizarPraia(99L, input))
                .isInstanceOf(PraiaNaoEncontradaException.class);

        verify(praiaRepository, never()).save(any());
    }

    @Test
    void deletarPraia_quandoExiste_deveRemover() {
        Praia praiaExistente = criarPraiaEntity();
        when(praiaRepository.findById(1L)).thenReturn(Optional.of(praiaExistente));

        praiaService.deletarPraia(1L);

        verify(praiaRepository, times(1)).delete(praiaExistente);
    }

    @Test
    void deletarPraia_quandoNaoExiste_deveLancarPraiaNaoEncontradaException() {
        when(praiaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> praiaService.deletarPraia(99L))
                .isInstanceOf(PraiaNaoEncontradaException.class);

        verify(praiaRepository, never()).delete(any());
    }
}

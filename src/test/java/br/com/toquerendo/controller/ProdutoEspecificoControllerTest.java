package br.com.toquerendo.controller;

import br.com.toquerendo.dto.input.AtualizarProdutoEspecificoInputDto;
import br.com.toquerendo.dto.input.CriarProdutoEspecificoInputDto;
import br.com.toquerendo.dto.output.ProdutoEspecificoOutputDto;
import br.com.toquerendo.exception.GlobalExceptionHandler;
import br.com.toquerendo.exception.ProdutoNaoEncontradoException;
import br.com.toquerendo.exception.ProdutoNaoPertenceAoVendedorException;
import br.com.toquerendo.exception.RuntimeApiException;
import br.com.toquerendo.service.implementation.ProdutoEspecificoServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProdutoEspecificoControllerTest {

    @Mock
    private ProdutoEspecificoServiceImpl produtoEspecificoService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ProdutoEspecificoController controller = new ProdutoEspecificoController(produtoEspecificoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(new LocalValidatorFactoryBean())
                .build();
    }

    private ProdutoEspecificoOutputDto criarOutput() {
        return ProdutoEspecificoOutputDto.builder()
                .id(1L)
                .nome("Água de coco gelada")
                .idProdutoBase(10L)
                .idVendedor(1L)
                .preco(BigDecimal.valueOf(6.5))
                .produtoAtivo(true)
                .build();
    }

    private CriarProdutoEspecificoInputDto criarInputValido() {
        CriarProdutoEspecificoInputDto input = new CriarProdutoEspecificoInputDto();
        input.setNome("Água de coco gelada");
        input.setIdProdutoBase(10L);
        input.setPreco(BigDecimal.valueOf(6.5));
        return input;
    }

    @Test
    void criarProdutoEspecifico_comDadosValidos_deveRetornar200() throws Exception {
        when(produtoEspecificoService.criarProdutoEspecifico(any())).thenReturn(criarOutput());

        mockMvc.perform(post("/produto-especifico")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.idVendedor").value(1))
                .andExpect(jsonPath("$.messages[0]").value("Produto criado com sucesso!"));
    }

    @Test
    void criarProdutoEspecifico_semNome_deveRetornar400() throws Exception {
        CriarProdutoEspecificoInputDto input = criarInputValido();
        input.setNome(null);

        mockMvc.perform(post("/produto-especifico")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarProdutoEspecifico_semIdProdutoBase_deveRetornar400() throws Exception {
        CriarProdutoEspecificoInputDto input = criarInputValido();
        input.setIdProdutoBase(null);

        mockMvc.perform(post("/produto-especifico")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarProdutoEspecifico_quandoProdutoBaseNaoEncontrado_deveRetornar404() throws Exception {
        when(produtoEspecificoService.criarProdutoEspecifico(any())).thenThrow(new ProdutoNaoEncontradoException());

        mockMvc.perform(post("/produto-especifico")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isNotFound());
    }

    @Test
    void criarProdutoEspecifico_quandoPrecoAbaixoDoMinimo_deveRetornar400() throws Exception {
        when(produtoEspecificoService.criarProdutoEspecifico(any()))
                .thenThrow(new RuntimeApiException("O preço do produto específico não pode ser menor que o preço mínimo do produto base."));

        mockMvc.perform(post("/produto-especifico")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizarProdutoEspecifico_comDadosValidos_deveRetornar200() throws Exception {
        when(produtoEspecificoService.atualizarProdutoEspecifico(anyLong(), any())).thenReturn(criarOutput());

        mockMvc.perform(put("/produto-especifico/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new AtualizarProdutoEspecificoInputDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0]").value("Produto atualizado com sucesso!"));
    }

    @Test
    void atualizarProdutoEspecifico_quandoNaoEDonoDoProduto_deveRetornar403() throws Exception {
        when(produtoEspecificoService.atualizarProdutoEspecifico(anyLong(), any()))
                .thenThrow(new ProdutoNaoPertenceAoVendedorException());

        mockMvc.perform(put("/produto-especifico/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new AtualizarProdutoEspecificoInputDto())))
                .andExpect(status().isForbidden());
    }

    @Test
    void atualizarProdutoEspecifico_quandoNaoEncontrado_deveRetornar404() throws Exception {
        when(produtoEspecificoService.atualizarProdutoEspecifico(anyLong(), any()))
                .thenThrow(new ProdutoNaoEncontradoException());

        mockMvc.perform(put("/produto-especifico/{id}", 99L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new AtualizarProdutoEspecificoInputDto())))
                .andExpect(status().isNotFound());
    }

    @Test
    void consultarProdutosAtivosDoVendedorLogado_deveRetornar200ComListaDeProdutos() throws Exception {
        when(produtoEspecificoService.consultarProdutosAtivosDoVendedorLogado()).thenReturn(List.of(criarOutput()));

        mockMvc.perform(get("/produto-especifico/meus-produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].produtoAtivo").value(true));
    }
}

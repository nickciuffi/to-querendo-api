package br.com.toquerendo.controller;

import br.com.toquerendo.dto.input.ConcluirIntencaoCompraInputDto;
import br.com.toquerendo.dto.input.CriarIntencaoCompraInputDto;
import br.com.toquerendo.dto.output.BanhistaComIntencoesOutputDto;
import br.com.toquerendo.dto.output.IntencaoCompraOutputDto;
import br.com.toquerendo.exception.GlobalExceptionHandler;
import br.com.toquerendo.exception.IntencaoCompraNaoEncontradaException;
import br.com.toquerendo.exception.ProdutoNaoEncontradoException;
import br.com.toquerendo.service.implementation.IntencaoCompraServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class IntencaoCompraControllerTest {

    @Mock
    private IntencaoCompraServiceImpl intencaoCompraService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        IntencaoCompraController controller = new IntencaoCompraController(intencaoCompraService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(new LocalValidatorFactoryBean())
                .build();
    }

    private IntencaoCompraOutputDto criarOutput() {
        return IntencaoCompraOutputDto.builder()
                .id(1L)
                .idUsuario(1L)
                .idProdutoBase(10L)
                .nomeProdutoBase("Água de coco")
                .descricaoLocal("Guarda-sol azul, próximo ao quiosque 3")
                .build();
    }

    private CriarIntencaoCompraInputDto criarInputValido() {
        CriarIntencaoCompraInputDto input = new CriarIntencaoCompraInputDto();
        input.setIdProdutoBase(10L);
        input.setDescricaoLocal("Guarda-sol azul, próximo ao quiosque 3");
        return input;
    }

    @Test
    void criarIntencaoCompra_comDadosValidos_deveRetornar200() throws Exception {
        when(intencaoCompraService.criarIntencaoCompra(any())).thenReturn(criarOutput());

        mockMvc.perform(post("/intencao-compra")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.idProdutoBase").value(10))
                .andExpect(jsonPath("$.messages[0]").value("Intenção de compra criada com sucesso!"));
    }

    @Test
    void criarIntencaoCompra_semIdProdutoBase_deveRetornar400() throws Exception {
        CriarIntencaoCompraInputDto input = criarInputValido();
        input.setIdProdutoBase(null);

        mockMvc.perform(post("/intencao-compra")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarIntencaoCompra_semDescricaoLocal_deveRetornar400() throws Exception {
        CriarIntencaoCompraInputDto input = criarInputValido();
        input.setDescricaoLocal(null);

        mockMvc.perform(post("/intencao-compra")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarIntencaoCompra_quandoProdutoBaseNaoEncontrado_deveRetornar404() throws Exception {
        when(intencaoCompraService.criarIntencaoCompra(any())).thenThrow(new ProdutoNaoEncontradoException());

        mockMvc.perform(post("/intencao-compra")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isNotFound());
    }

    @Test
    void consultarBanhistasComIntencaoDeCompra_comBanhistasEncontrados_deveRetornar200ComListaEIntencoesAninhadas() throws Exception {
        BanhistaComIntencoesOutputDto banhista = BanhistaComIntencoesOutputDto.builder()
                .idUsuario(50L)
                .nomeUsuario("Banhista A")
                .intencoesCompra(List.of(criarOutput()))
                .build();

        when(intencaoCompraService.consultarBanhistasComIntencaoDeCompra()).thenReturn(List.of(banhista));

        mockMvc.perform(get("/intencao-compra/banhistas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].idUsuario").value(50))
                .andExpect(jsonPath("$.response[0].intencoesCompra[0].idProdutoBase").value(10))
                .andExpect(jsonPath("$.messages[0]").value("Banhistas consultados com sucesso!"));
    }

    @Test
    void consultarBanhistasComIntencaoDeCompra_semBanhistas_deveRetornar200ComListaVazia() throws Exception {
        when(intencaoCompraService.consultarBanhistasComIntencaoDeCompra()).thenReturn(List.of());

        mockMvc.perform(get("/intencao-compra/banhistas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").isEmpty());
    }

    private ConcluirIntencaoCompraInputDto criarInputConclusaoValido() {
        ConcluirIntencaoCompraInputDto input = new ConcluirIntencaoCompraInputDto();
        input.setIdProdutoBase(10L);
        input.setMotivo("Já comprei");
        return input;
    }

    private ResultActions concluir(ConcluirIntencaoCompraInputDto input) throws Exception {
        return mockMvc.perform(delete("/intencao-compra")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(input)));
    }

    @Test
    void concluirIntencaoCompra_comDadosValidos_deveRetornar200EChamarServico() throws Exception {
        concluir(criarInputConclusaoValido())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0]").value("Intenção de compra concluída com sucesso!"));

        verify(intencaoCompraService).concluirIntencaoCompra(any(ConcluirIntencaoCompraInputDto.class));
    }

    @Test
    void concluirIntencaoCompra_semIdProdutoBase_deveRetornar400() throws Exception {
        ConcluirIntencaoCompraInputDto input = criarInputConclusaoValido();
        input.setIdProdutoBase(null);

        concluir(input)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[1]").value("idProdutoBase: O id do produto base é obrigatório"));

        verify(intencaoCompraService, never()).concluirIntencaoCompra(any());
    }

    @Test
    void concluirIntencaoCompra_semMotivo_deveRetornar400() throws Exception {
        ConcluirIntencaoCompraInputDto input = criarInputConclusaoValido();
        input.setMotivo(null);

        concluir(input).andExpect(status().isBadRequest());

        verify(intencaoCompraService, never()).concluirIntencaoCompra(any());
    }

    @Test
    void concluirIntencaoCompra_comMotivoEmBranco_deveRetornar400() throws Exception {
        ConcluirIntencaoCompraInputDto input = criarInputConclusaoValido();
        input.setMotivo("   ");

        concluir(input).andExpect(status().isBadRequest());

        verify(intencaoCompraService, never()).concluirIntencaoCompra(any());
    }

    @Test
    void concluirIntencaoCompra_comMotivoMaiorQue200Caracteres_deveRetornar400() throws Exception {
        ConcluirIntencaoCompraInputDto input = criarInputConclusaoValido();
        input.setMotivo("a".repeat(201));

        concluir(input).andExpect(status().isBadRequest());

        verify(intencaoCompraService, never()).concluirIntencaoCompra(any());
    }

    @Test
    void concluirIntencaoCompra_comMotivoDe200Caracteres_deveRetornar200() throws Exception {
        ConcluirIntencaoCompraInputDto input = criarInputConclusaoValido();
        input.setMotivo("a".repeat(200));

        concluir(input).andExpect(status().isOk());
    }

    @Test
    void concluirIntencaoCompra_quandoNaoHaIntencaoAtiva_deveRetornar404() throws Exception {
        doThrow(new IntencaoCompraNaoEncontradaException())
                .when(intencaoCompraService).concluirIntencaoCompra(any());

        concluir(criarInputConclusaoValido()).andExpect(status().isNotFound());
    }
}

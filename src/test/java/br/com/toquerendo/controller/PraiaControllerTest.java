package br.com.toquerendo.controller;

import br.com.toquerendo.dto.input.AtualizarPraiaInputDto;
import br.com.toquerendo.dto.input.CriarPraiaInputDto;
import br.com.toquerendo.dto.output.PraiaOutputDto;
import br.com.toquerendo.exception.GlobalExceptionHandler;
import br.com.toquerendo.exception.PraiaNaoEncontradaException;
import br.com.toquerendo.service.implementation.PraiaServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PraiaControllerTest {

    @Mock
    private PraiaServiceImpl praiaService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        PraiaController controller = new PraiaController(praiaService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(new LocalValidatorFactoryBean())
                .build();
    }

    private PraiaOutputDto criarPraiaOutput() {
        return PraiaOutputDto.builder()
                .id(1L)
                .nome("Praia de Boa Viagem")
                .cidade("Recife")
                .estado("PE")
                .build();
    }

    @Test
    void consultarPraias_deveRetornar200ComListaDePraias() throws Exception {
        when(praiaService.consultarPraias()).thenReturn(List.of(criarPraiaOutput()));

        mockMvc.perform(get("/praia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].nome").value("Praia de Boa Viagem"))
                .andExpect(jsonPath("$.messages[0]").value("Praias consultadas com sucesso!"));
    }

    @Test
    void consultarPraiaPorId_quandoExiste_deveRetornar200() throws Exception {
        when(praiaService.consultarPraiaPorId(1L)).thenReturn(criarPraiaOutput());

        mockMvc.perform(get("/praia/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.id").value(1))
                .andExpect(jsonPath("$.response.nome").value("Praia de Boa Viagem"));
    }

    @Test
    void consultarPraiaPorId_quandoNaoExiste_deveRetornar404() throws Exception {
        when(praiaService.consultarPraiaPorId(99L)).thenThrow(new PraiaNaoEncontradaException());

        mockMvc.perform(get("/praia/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.messages[0]").value("Praia não encontrada para os critérios informados."));
    }

    @Test
    void criarPraia_comDadosValidos_deveRetornar200() throws Exception {
        CriarPraiaInputDto input = new CriarPraiaInputDto();
        input.setNome("Praia de Boa Viagem");
        input.setCidade("Recife");
        input.setEstado("PE");

        when(praiaService.criarPraia(any())).thenReturn(criarPraiaOutput());

        mockMvc.perform(post("/praia")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.id").value(1))
                .andExpect(jsonPath("$.messages[0]").value("Praia criada com sucesso!"));
    }

    @Test
    void criarPraia_semNome_deveRetornar400() throws Exception {
        CriarPraiaInputDto input = new CriarPraiaInputDto();
        input.setCidade("Recife");
        input.setEstado("PE");
        // nome ausente, viola @NotNull

        mockMvc.perform(post("/praia")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarPraia_comEstadoInvalido_deveRetornar400() throws Exception {
        CriarPraiaInputDto input = new CriarPraiaInputDto();
        input.setNome("Praia de Boa Viagem");
        input.setCidade("Recife");
        input.setEstado("PER"); // viola @Size(min = 2, max = 2)

        mockMvc.perform(post("/praia")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizarPraia_quandoExiste_deveRetornar200() throws Exception {
        AtualizarPraiaInputDto input = new AtualizarPraiaInputDto();
        input.setNome("Novo nome");

        when(praiaService.atualizarPraia(anyLong(), any())).thenReturn(criarPraiaOutput());

        mockMvc.perform(put("/praia/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0]").value("Praia atualizada com sucesso!"));
    }

    @Test
    void atualizarPraia_quandoNaoExiste_deveRetornar404() throws Exception {
        AtualizarPraiaInputDto input = new AtualizarPraiaInputDto();
        input.setNome("Novo nome");

        when(praiaService.atualizarPraia(anyLong(), any())).thenThrow(new PraiaNaoEncontradaException());

        mockMvc.perform(put("/praia/{id}", 99L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletarPraia_quandoExiste_deveRetornar200() throws Exception {
        mockMvc.perform(delete("/praia/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0]").value("Praia removida com sucesso!"));
    }

    @Test
    void deletarPraia_quandoNaoExiste_deveRetornar404() throws Exception {
        org.mockito.Mockito.doThrow(new PraiaNaoEncontradaException()).when(praiaService).deletarPraia(99L);

        mockMvc.perform(delete("/praia/{id}", 99L))
                .andExpect(status().isNotFound());
    }
}

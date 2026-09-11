package br.com.toquerendo.controller;

import br.com.toquerendo.dto.input.AtualizarLocalizacaoInputDto;
import br.com.toquerendo.dto.output.LocalizacaoOutputDto;
import br.com.toquerendo.dto.output.VendedorLocalizacaoOutputDto;
import br.com.toquerendo.exception.GlobalExceptionHandler;
import br.com.toquerendo.exception.PraiaNaoEncontradaException;
import br.com.toquerendo.exception.ProdutoNaoEncontradoException;
import br.com.toquerendo.service.implementation.LocalizacaoServiceImpl;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LocalizacaoControllerTest {

    @Mock
    private LocalizacaoServiceImpl localizacaoService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocalizacaoController controller = new LocalizacaoController(localizacaoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(new LocalValidatorFactoryBean())
                .build();
    }

    private LocalizacaoOutputDto criarOutput() {
        return LocalizacaoOutputDto.builder()
                .idUsuario(1L)
                .latitude(BigDecimal.valueOf(-23.5505199))
                .longitude(BigDecimal.valueOf(-46.6333094))
                .build();
    }

    private AtualizarLocalizacaoInputDto criarInputValido() {
        AtualizarLocalizacaoInputDto input = new AtualizarLocalizacaoInputDto();
        input.setLatitude(BigDecimal.valueOf(-23.5505199));
        input.setLongitude(BigDecimal.valueOf(-46.6333094));
        return input;
    }

    private VendedorLocalizacaoOutputDto criarVendedorLocalizacaoOutput() {
        return VendedorLocalizacaoOutputDto.builder()
                .idVendedor(1L)
                .nomeVendedor("João da Praia")
                .idProdutoEspecifico(5L)
                .nomeProduto("Água de coco gelada")
                .preco(BigDecimal.valueOf(6.5))
                .latitude(BigDecimal.valueOf(-23.5505199))
                .longitude(BigDecimal.valueOf(-46.6333094))
                .build();
    }

    @Test
    void atualizarLocalizacao_comDadosValidos_deveRetornar200() throws Exception {
        when(localizacaoService.atualizarLocalizacao(any())).thenReturn(criarOutput());

        mockMvc.perform(put("/localizacao")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.idUsuario").value(1))
                .andExpect(jsonPath("$.messages[0]").value("Localização atualizada com sucesso!"));
    }

    @Test
    void atualizarLocalizacao_semLatitude_deveRetornar400() throws Exception {
        AtualizarLocalizacaoInputDto input = criarInputValido();
        input.setLatitude(null);

        mockMvc.perform(put("/localizacao")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizarLocalizacao_semLongitude_deveRetornar400() throws Exception {
        AtualizarLocalizacaoInputDto input = criarInputValido();
        input.setLongitude(null);

        mockMvc.perform(put("/localizacao")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizarLocalizacao_comLatitudeForaDoIntervalo_deveRetornar400() throws Exception {
        AtualizarLocalizacaoInputDto input = criarInputValido();
        input.setLatitude(BigDecimal.valueOf(-91));

        mockMvc.perform(put("/localizacao")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizarLocalizacao_comLongitudeForaDoIntervalo_deveRetornar400() throws Exception {
        AtualizarLocalizacaoInputDto input = criarInputValido();
        input.setLongitude(BigDecimal.valueOf(181));

        mockMvc.perform(put("/localizacao")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void consultarLocalizacaoVendedores_comDadosValidos_deveRetornar200ComListaDeVendedores() throws Exception {
        when(localizacaoService.consultarLocalizacaoVendedores(anyLong(), anyLong()))
                .thenReturn(List.of(criarVendedorLocalizacaoOutput()));

        mockMvc.perform(get("/localizacao/vendedores")
                        .param("idProdutoBase", "10")
                        .param("idPraia", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].idVendedor").value(1))
                .andExpect(jsonPath("$.response[0].latitude").value(-23.5505199))
                .andExpect(jsonPath("$.messages[0]").value("Localizações consultadas com sucesso!"));
    }

    @Test
    void consultarLocalizacaoVendedores_quandoNenhumVendedorDisponivel_deveRetornar200ComListaVazia() throws Exception {
        when(localizacaoService.consultarLocalizacaoVendedores(anyLong(), anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/localizacao/vendedores")
                        .param("idProdutoBase", "10")
                        .param("idPraia", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").isEmpty());
    }

    @Test
    void consultarLocalizacaoVendedores_quandoProdutoBaseNaoEncontrado_deveRetornar404() throws Exception {
        when(localizacaoService.consultarLocalizacaoVendedores(anyLong(), anyLong()))
                .thenThrow(new ProdutoNaoEncontradoException());

        mockMvc.perform(get("/localizacao/vendedores")
                        .param("idProdutoBase", "10")
                        .param("idPraia", "3"))
                .andExpect(status().isNotFound());
    }

    @Test
    void consultarLocalizacaoVendedores_quandoPraiaNaoEncontrada_deveRetornar404() throws Exception {
        when(localizacaoService.consultarLocalizacaoVendedores(anyLong(), anyLong()))
                .thenThrow(new PraiaNaoEncontradaException());

        mockMvc.perform(get("/localizacao/vendedores")
                        .param("idProdutoBase", "10")
                        .param("idPraia", "3"))
                .andExpect(status().isNotFound());
    }

    @Test
    void consultarLocalizacaoVendedores_semIdProdutoBase_deveRetornar400() throws Exception {
        mockMvc.perform(get("/localizacao/vendedores")
                        .param("idPraia", "3"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void consultarLocalizacaoVendedores_semIdPraia_deveRetornar400() throws Exception {
        mockMvc.perform(get("/localizacao/vendedores")
                        .param("idProdutoBase", "10"))
                .andExpect(status().isBadRequest());
    }
}

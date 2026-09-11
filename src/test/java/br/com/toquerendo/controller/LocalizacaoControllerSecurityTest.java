package br.com.toquerendo.controller;

import br.com.toquerendo.dto.input.AtualizarLocalizacaoInputDto;
import br.com.toquerendo.dto.output.LocalizacaoOutputDto;
import br.com.toquerendo.security.JwtService;
import br.com.toquerendo.service.implementation.LocalizacaoServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica que o {@code @PreAuthorize} aplicado via {@code @TuristaOnly} permite turistas e
 * vendedores (que também carregam ROLE_TURISTA) atualizarem sua localização, negando apenas ADMIN.
 */
@WebMvcTest(controllers = LocalizacaoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(LocalizacaoControllerSecurityTest.MethodSecurityTestConfig.class)
class LocalizacaoControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocalizacaoServiceImpl localizacaoService;

    @MockBean
    private JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AtualizarLocalizacaoInputDto criarInputValido() {
        AtualizarLocalizacaoInputDto input = new AtualizarLocalizacaoInputDto();
        input.setLatitude(BigDecimal.valueOf(-23.5505199));
        input.setLongitude(BigDecimal.valueOf(-46.6333094));
        return input;
    }

    @Test
    @WithMockUser(roles = "TURISTA")
    void atualizarLocalizacao_comRoleTurista_deveSerPermitido() throws Exception {
        when(localizacaoService.atualizarLocalizacao(any()))
                .thenReturn(LocalizacaoOutputDto.builder().idUsuario(1L).build());

        mockMvc.perform(put("/localizacao")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"TURISTA", "VENDEDOR"})
    void atualizarLocalizacao_comRoleVendedor_deveSerPermitido() throws Exception {
        when(localizacaoService.atualizarLocalizacao(any()))
                .thenReturn(LocalizacaoOutputDto.builder().idUsuario(1L).build());

        mockMvc.perform(put("/localizacao")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarLocalizacao_comRoleAdmin_deveSerNegado() throws Exception {
        mockMvc.perform(put("/localizacao")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TURISTA")
    void consultarLocalizacaoVendedores_comRoleTurista_deveSerPermitido() throws Exception {
        when(localizacaoService.consultarLocalizacaoVendedores(anyLong(), anyLong()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/localizacao/vendedores")
                        .param("idProdutoBase", "10")
                        .param("idPraia", "3"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void consultarLocalizacaoVendedores_comRoleAdmin_deveSerNegado() throws Exception {
        mockMvc.perform(get("/localizacao/vendedores")
                        .param("idProdutoBase", "10")
                        .param("idPraia", "3"))
                .andExpect(status().isForbidden());
    }

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {
    }
}

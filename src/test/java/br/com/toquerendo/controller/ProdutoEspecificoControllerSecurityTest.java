package br.com.toquerendo.controller;

import br.com.toquerendo.dto.input.AtualizarProdutoEspecificoInputDto;
import br.com.toquerendo.dto.input.CriarProdutoEspecificoInputDto;
import br.com.toquerendo.dto.output.ProdutoEspecificoOutputDto;
import br.com.toquerendo.security.JwtService;
import br.com.toquerendo.service.implementation.ProdutoEspecificoServiceImpl;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica que o {@code @PreAuthorize} aplicado via {@code @VendedorOnly} restringe criação, edição e
 * listagem dos próprios produtos a usuários com ROLE_VENDEDOR.
 */
@WebMvcTest(controllers = ProdutoEspecificoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ProdutoEspecificoControllerSecurityTest.MethodSecurityTestConfig.class)
class ProdutoEspecificoControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoEspecificoServiceImpl produtoEspecificoService;

    @MockBean
    private JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CriarProdutoEspecificoInputDto criarInputValido() {
        CriarProdutoEspecificoInputDto input = new CriarProdutoEspecificoInputDto();
        input.setNome("Água de coco gelada");
        input.setIdProdutoBase(10L);
        input.setPreco(BigDecimal.valueOf(6.5));
        return input;
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void criarProdutoEspecifico_comRoleVendedor_deveSerPermitido() throws Exception {
        when(produtoEspecificoService.criarProdutoEspecifico(any()))
                .thenReturn(ProdutoEspecificoOutputDto.builder().id(1L).build());

        mockMvc.perform(post("/produto-especifico")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TURISTA")
    void criarProdutoEspecifico_comRoleTurista_deveSerNegado() throws Exception {
        mockMvc.perform(post("/produto-especifico")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criarProdutoEspecifico_comRoleAdmin_deveSerNegado() throws Exception {
        mockMvc.perform(post("/produto-especifico")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void atualizarProdutoEspecifico_comRoleVendedor_deveSerPermitido() throws Exception {
        when(produtoEspecificoService.atualizarProdutoEspecifico(anyLong(), any()))
                .thenReturn(ProdutoEspecificoOutputDto.builder().id(1L).build());

        mockMvc.perform(put("/produto-especifico/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new AtualizarProdutoEspecificoInputDto())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TURISTA")
    void atualizarProdutoEspecifico_comRoleTurista_deveSerNegado() throws Exception {
        mockMvc.perform(put("/produto-especifico/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new AtualizarProdutoEspecificoInputDto())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void consultarProdutosAtivosDoVendedorLogado_comRoleVendedor_deveSerPermitido() throws Exception {
        when(produtoEspecificoService.consultarProdutosAtivosDoVendedorLogado()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/produto-especifico/meus-produtos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TURISTA")
    void consultarProdutosAtivosDoVendedorLogado_comRoleTurista_deveSerNegado() throws Exception {
        mockMvc.perform(get("/produto-especifico/meus-produtos"))
                .andExpect(status().isForbidden());
    }

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {
    }
}

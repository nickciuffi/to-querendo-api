package br.com.toquerendo.controller;

import br.com.toquerendo.dto.input.AtualizarPraiaInputDto;
import br.com.toquerendo.dto.input.CriarPraiaInputDto;
import br.com.toquerendo.dto.output.PraiaOutputDto;
import br.com.toquerendo.security.JwtService;
import br.com.toquerendo.service.implementation.PraiaServiceImpl;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static java.util.Collections.emptyList;

/**
 * Verifica, isoladamente da cadeia de filtros HTTP, que o {@code @PreAuthorize} aplicado via
 * {@code @AdminOnly} realmente restringe criação/edição/remoção de praias a usuários com ROLE_ADMIN,
 * enquanto a consulta permanece acessível a qualquer role autenticada.
 */
@WebMvcTest(controllers = PraiaController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(PraiaControllerSecurityTest.MethodSecurityTestConfig.class)
class PraiaControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PraiaServiceImpl praiaService;

    @MockBean
    private JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CriarPraiaInputDto criarInputValido() {
        CriarPraiaInputDto input = new CriarPraiaInputDto();
        input.setNome("Praia Nova");
        input.setCidade("Recife");
        input.setEstado("PE");
        return input;
    }

    @Test
    @WithMockUser(roles = "TURISTA")
    void consultarPraias_comQualquerRoleAutenticada_deveSerPermitido() throws Exception {
        when(praiaService.consultarPraias()).thenReturn(emptyList());

        mockMvc.perform(get("/praia"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void consultarPraiaPorId_comQualquerRoleAutenticada_deveSerPermitido() throws Exception {
        when(praiaService.consultarPraiaPorId(1L)).thenReturn(PraiaOutputDto.builder().id(1L).build());

        mockMvc.perform(get("/praia/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criarPraia_comRoleAdmin_deveSerPermitido() throws Exception {
        when(praiaService.criarPraia(any())).thenReturn(PraiaOutputDto.builder().id(1L).build());

        mockMvc.perform(post("/praia")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TURISTA")
    void criarPraia_comRoleTurista_deveSerNegado() throws Exception {
        mockMvc.perform(post("/praia")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void criarPraia_comRoleVendedor_deveSerNegado() throws Exception {
        mockMvc.perform(post("/praia")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarPraia_comRoleAdmin_deveSerPermitido() throws Exception {
        when(praiaService.atualizarPraia(anyLong(), any())).thenReturn(PraiaOutputDto.builder().id(1L).build());

        mockMvc.perform(put("/praia/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new AtualizarPraiaInputDto())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TURISTA")
    void atualizarPraia_comRoleTurista_deveSerNegado() throws Exception {
        mockMvc.perform(put("/praia/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new AtualizarPraiaInputDto())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TURISTA")
    void deletarPraia_comRoleTurista_deveSerNegado() throws Exception {
        mockMvc.perform(delete("/praia/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletarPraia_comRoleAdmin_deveSerPermitido() throws Exception {
        mockMvc.perform(delete("/praia/{id}", 1L))
                .andExpect(status().isOk());
    }

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {
    }
}

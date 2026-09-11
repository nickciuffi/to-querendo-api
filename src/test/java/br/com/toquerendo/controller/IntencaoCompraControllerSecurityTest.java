package br.com.toquerendo.controller;

import br.com.toquerendo.dto.input.CriarIntencaoCompraInputDto;
import br.com.toquerendo.dto.output.IntencaoCompraOutputDto;
import br.com.toquerendo.security.JwtService;
import br.com.toquerendo.service.implementation.IntencaoCompraServiceImpl;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica que o {@code @PreAuthorize} aplicado via {@code @TuristaOnly} restringe a criação de
 * intenções de compra a usuários com ROLE_TURISTA.
 */
@WebMvcTest(controllers = IntencaoCompraController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(IntencaoCompraControllerSecurityTest.MethodSecurityTestConfig.class)
class IntencaoCompraControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IntencaoCompraServiceImpl intencaoCompraService;

    @MockBean
    private JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CriarIntencaoCompraInputDto criarInputValido() {
        CriarIntencaoCompraInputDto input = new CriarIntencaoCompraInputDto();
        input.setIdProdutoBase(10L);
        input.setDescricaoLocal("Guarda-sol azul, próximo ao quiosque 3");
        return input;
    }

    @Test
    @WithMockUser(roles = "TURISTA")
    void criarIntencaoCompra_comRoleTurista_deveSerPermitido() throws Exception {
        when(intencaoCompraService.criarIntencaoCompra(any()))
                .thenReturn(IntencaoCompraOutputDto.builder().id(1L).build());

        mockMvc.perform(post("/intencao-compra")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void criarIntencaoCompra_comRoleVendedorSemTurista_deveSerNegado() throws Exception {
        mockMvc.perform(post("/intencao-compra")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criarIntencaoCompra_comRoleAdmin_deveSerNegado() throws Exception {
        mockMvc.perform(post("/intencao-compra")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarInputValido())))
                .andExpect(status().isForbidden());
    }

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {
    }
}

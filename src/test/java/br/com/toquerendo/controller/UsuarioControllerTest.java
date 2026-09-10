package br.com.toquerendo.controller;

import br.com.toquerendo.dto.output.UsuarioOutputDto;
import br.com.toquerendo.exception.GlobalExceptionHandler;
import br.com.toquerendo.exception.UsuarioNaoAutorizadoException;
import br.com.toquerendo.service.implementation.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioServiceImpl usuarioService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        UsuarioController controller = new UsuarioController(usuarioService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void consultarUsuarioAutenticado_deveRetornar200ComDadosDoUsuario() throws Exception {
        UsuarioOutputDto output = UsuarioOutputDto.builder()
                .email("usuario@email.com")
                .nome("Fulano de Tal")
                .categoria("Turista")
                .praiaAtual("Sem praia definida")
                .build();

        when(usuarioService.consultarUsuarioAutenticado()).thenReturn(output);

        mockMvc.perform(get("/usuario/meus-dados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.email").value("usuario@email.com"))
                .andExpect(jsonPath("$.response.nome").value("Fulano de Tal"))
                .andExpect(jsonPath("$.messages[0]").value("Dados do usuário consultados com sucesso!"));
    }

    @Test
    void consultarUsuarioAutenticado_quandoNaoAutenticado_deveRetornar403() throws Exception {
        when(usuarioService.consultarUsuarioAutenticado()).thenThrow(new UsuarioNaoAutorizadoException());

        mockMvc.perform(get("/usuario/meus-dados"))
                .andExpect(status().isForbidden());
    }
}

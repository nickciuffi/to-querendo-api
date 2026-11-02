package br.com.toquerendo.controller;

import br.com.toquerendo.dto.output.ArquivoUploadOutputDto;
import br.com.toquerendo.exception.GlobalExceptionHandler;
import br.com.toquerendo.exception.PastaArquivoNaoPermitidaException;
import br.com.toquerendo.exception.RuntimeApiException;
import br.com.toquerendo.exception.UploadArquivoException;
import br.com.toquerendo.service.ArquivoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ArquivoControllerTest {

    @Mock
    private ArquivoService arquivoService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ArquivoController(arquivoService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private MockMultipartFile arquivo() {
        return new MockMultipartFile("arquivo", "foto.jpg", "image/jpeg", new byte[]{1, 2, 3});
    }

    @Test
    void upload_comDadosValidos_deveRetornar200ComPathEUrl() throws Exception {
        when(arquivoService.upload(eq("usuarios"), any())).thenReturn(ArquivoUploadOutputDto.builder()
                .path("/usuarios/abc.jpeg")
                .url("https://bucket.exemplo.com/usuarios/abc.jpeg")
                .build());

        mockMvc.perform(multipart("/arquivo").file(arquivo()).param("pasta", "usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.path").value("/usuarios/abc.jpeg"))
                .andExpect(jsonPath("$.response.url").value("https://bucket.exemplo.com/usuarios/abc.jpeg"))
                .andExpect(jsonPath("$.messages[0]").value("Arquivo enviado com sucesso!"));
    }

    @Test
    void upload_semArquivo_deveRetornar400() throws Exception {
        mockMvc.perform(multipart("/arquivo").param("pasta", "usuarios"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("Parte obrigatória ausente: arquivo"));

        verify(arquivoService, never()).upload(any(), any());
    }

    @Test
    void upload_semPasta_deveRetornar400() throws Exception {
        mockMvc.perform(multipart("/arquivo").file(arquivo()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("Parâmetro obrigatório ausente: pasta"));

        verify(arquivoService, never()).upload(any(), any());
    }

    @Test
    void upload_quandoPastaOuArquivoInvalidos_deveRetornar400() throws Exception {
        when(arquivoService.upload(any(), any())).thenThrow(new RuntimeApiException("Pasta inválida."));

        mockMvc.perform(multipart("/arquivo").file(arquivo()).param("pasta", "x"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("Pasta inválida."));
    }

    @Test
    void upload_quandoSemPermissaoNaPasta_deveRetornar403() throws Exception {
        when(arquivoService.upload(any(), any())).thenThrow(new PastaArquivoNaoPermitidaException());

        mockMvc.perform(multipart("/arquivo").file(arquivo()).param("pasta", "praias"))
                .andExpect(status().isForbidden());
    }

    @Test
    void upload_quandoArquivoExcedeLimite_deveRetornar413() throws Exception {
        when(arquivoService.upload(any(), any())).thenThrow(new MaxUploadSizeExceededException(5 * 1024 * 1024));

        mockMvc.perform(multipart("/arquivo").file(arquivo()).param("pasta", "usuarios"))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.messages[0]").value("O arquivo excede o tamanho máximo permitido de 5 MB."));
    }

    @Test
    void upload_quandoFalhaNoArmazenamento_deveRetornar500() throws Exception {
        when(arquivoService.upload(any(), any())).thenThrow(new UploadArquivoException());

        mockMvc.perform(multipart("/arquivo").file(arquivo()).param("pasta", "usuarios"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.messages[0]").value("Não foi possível enviar o arquivo."));
    }

    @Test
    void upload_comContentTypeJson_deveRetornar415() throws Exception {
        mockMvc.perform(post("/arquivo").contentType("application/json").content("{}"))
                .andExpect(status().isUnsupportedMediaType());

        verify(arquivoService, never()).upload(any(), any());
    }
}

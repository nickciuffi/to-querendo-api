package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.output.ArquivoUploadOutputDto;
import br.com.toquerendo.exception.PastaArquivoNaoPermitidaException;
import br.com.toquerendo.exception.RuntimeApiException;
import br.com.toquerendo.exception.UploadArquivoException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArquivoServiceImplTest {

    private static final String BUCKET = "meu-bucket";
    private static final String URL_BASE = "https://meu-bucket.s3.sa-east-1.amazonaws.com";

    @Mock
    private S3Client s3Client;

    private ArquivoServiceImpl arquivoService;

    @BeforeEach
    void setUp() {
        arquivoService = new ArquivoServiceImpl(s3Client, BUCKET, URL_BASE);
        autenticarComRoles("ROLE_TURISTA");
    }

    @AfterEach
    void limparContextoDeSeguranca() {
        SecurityContextHolder.clearContext();
    }

    private void autenticarComRoles(String... roles) {
        List<SimpleGrantedAuthority> authorities = Arrays.stream(roles).map(SimpleGrantedAuthority::new).toList();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("usuario@email.com", null, authorities));
    }

    private byte[] bytesJpeg() {
        return new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0x10, 'J', 'F', 'I', 'F', 0, 1, 2, 3};
    }

    private byte[] bytesPng() {
        return new byte[]{(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0x0D, 'I', 'H', 'D', 'R'};
    }

    private byte[] bytesWebp() {
        return new byte[]{'R', 'I', 'F', 'F', 10, 0, 0, 0, 'W', 'E', 'B', 'P', 'V', 'P', '8', ' '};
    }

    private MockMultipartFile arquivo(byte[] conteudo) {
        return new MockMultipartFile("arquivo", "foto.original.jpg", "image/jpeg", conteudo);
    }

    @Test
    void upload_comJpegValido_deveGravarNoS3ComChaveGeradaERetornarPathEUrl() {
        ArquivoUploadOutputDto output = arquivoService.upload("usuarios", arquivo(bytesJpeg()));

        ArgumentCaptor<PutObjectRequest> requisicaoCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> corpoCaptor = ArgumentCaptor.forClass(RequestBody.class);
        verify(s3Client).putObject(requisicaoCaptor.capture(), corpoCaptor.capture());

        PutObjectRequest requisicao = requisicaoCaptor.getValue();
        assertThat(requisicao.bucket()).isEqualTo(BUCKET);
        assertThat(requisicao.key()).matches("usuarios/[0-9a-f-]{36}\\.jpeg");
        assertThat(requisicao.contentType()).isEqualTo("image/jpeg");
        assertThat(requisicao.cacheControl()).contains("max-age=31536000");
        assertThat(corpoCaptor.getValue().optionalContentLength()).contains((long) bytesJpeg().length);

        assertThat(output.getPath()).isEqualTo("/" + requisicao.key());
        assertThat(output.getUrl()).isEqualTo(URL_BASE + "/" + requisicao.key());
    }

    @Test
    void upload_comPng_deveUsarExtensaoETipoPng() {
        ArquivoUploadOutputDto output = arquivoService.upload("usuarios", arquivo(bytesPng()));

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(captor.capture(), any(RequestBody.class));
        assertThat(captor.getValue().contentType()).isEqualTo("image/png");
        assertThat(output.getPath()).matches("/usuarios/[0-9a-f-]{36}\\.png");
    }

    @Test
    void upload_comWebp_deveUsarExtensaoETipoWebp() {
        ArquivoUploadOutputDto output = arquivoService.upload("usuarios", arquivo(bytesWebp()));

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(captor.capture(), any(RequestBody.class));
        assertThat(captor.getValue().contentType()).isEqualTo("image/webp");
        assertThat(output.getPath()).matches("/usuarios/[0-9a-f-]{36}\\.webp");
    }

    @Test
    void upload_deveIgnorarContentTypeENomeEnviadosPeloClienteEUsarOTipoReal() {
        MockMultipartFile pngDisfarcadoDeJpg =
                new MockMultipartFile("arquivo", "../../etc/passwd.jpg", "image/jpeg", bytesPng());

        ArquivoUploadOutputDto output = arquivoService.upload("usuarios", pngDisfarcadoDeJpg);

        assertThat(output.getPath()).matches("/usuarios/[0-9a-f-]{36}\\.png");
        assertThat(output.getPath()).doesNotContain("..").doesNotContain("passwd");
    }

    @Test
    void upload_comConteudoQueNaoEImagem_deveLancarRuntimeApiExceptionSemGravar() {
        MockMultipartFile html = new MockMultipartFile("arquivo", "foto.png", "image/png",
                "<html><script>alert(1)</script></html>".getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> arquivoService.upload("usuarios", html))
                .isInstanceOf(RuntimeApiException.class)
                .hasMessageContaining("Tipo de arquivo não suportado");

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void upload_comArquivoVazio_deveLancarRuntimeApiExceptionSemGravar() {
        assertThatThrownBy(() -> arquivoService.upload("usuarios", arquivo(new byte[0])))
                .isInstanceOf(RuntimeApiException.class)
                .hasMessageContaining("arquivo");

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void upload_comArquivoNulo_deveLancarRuntimeApiException() {
        assertThatThrownBy(() -> arquivoService.upload("usuarios", null))
                .isInstanceOf(RuntimeApiException.class);
    }

    @Test
    void upload_comPastaInvalida_deveLancarRuntimeApiExceptionListandoAsPastasAceitas() {
        assertThatThrownBy(() -> arquivoService.upload("../outra-pasta", arquivo(bytesJpeg())))
                .isInstanceOf(RuntimeApiException.class)
                .hasMessageContaining("Pasta inválida")
                .hasMessageContaining("usuarios");

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void upload_comPastaEmMaiusculaEEspacos_deveSerAceita() {
        ArquivoUploadOutputDto output = arquivoService.upload("  Usuarios ", arquivo(bytesJpeg()));

        assertThat(output.getPath()).startsWith("/usuarios/");
    }

    @Test
    void upload_paraPastaDeAdminSemSerAdmin_deveLancarPastaArquivoNaoPermitidaException() {
        assertThatThrownBy(() -> arquivoService.upload("praias", arquivo(bytesJpeg())))
                .isInstanceOf(PastaArquivoNaoPermitidaException.class);
        assertThatThrownBy(() -> arquivoService.upload("produtos-base", arquivo(bytesJpeg())))
                .isInstanceOf(PastaArquivoNaoPermitidaException.class);

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void upload_paraPastaDeAdminComoAdmin_deveGravar() {
        autenticarComRoles("ROLE_TURISTA", "ROLE_VENDEDOR", "ROLE_ADMIN");

        assertThat(arquivoService.upload("praias", arquivo(bytesJpeg())).getPath()).startsWith("/praias/");
        assertThat(arquivoService.upload("produtos-base", arquivo(bytesJpeg())).getPath()).startsWith("/produtos-base/");
    }

    @Test
    void upload_paraProdutosEspecificosSemSerVendedor_deveLancarPastaArquivoNaoPermitidaException() {
        assertThatThrownBy(() -> arquivoService.upload("produtos-especificos", arquivo(bytesJpeg())))
                .isInstanceOf(PastaArquivoNaoPermitidaException.class);
    }

    @Test
    void upload_paraProdutosEspecificosComoVendedor_deveGravar() {
        autenticarComRoles("ROLE_TURISTA", "ROLE_VENDEDOR");

        assertThat(arquivoService.upload("produtos-especificos", arquivo(bytesJpeg())).getPath())
                .startsWith("/produtos-especificos/");
    }

    @Test
    void upload_paraPastasLiberadasComoTurista_deveGravar() {
        assertThat(arquivoService.upload("usuarios", arquivo(bytesJpeg())).getPath()).startsWith("/usuarios/");
        assertThat(arquivoService.upload("intencoes-compra", arquivo(bytesJpeg())).getPath()).startsWith("/intencoes-compra/");
    }

    @Test
    void upload_doisArquivosIguais_devemGerarChavesDiferentes() {
        String primeiro = arquivoService.upload("usuarios", arquivo(bytesJpeg())).getPath();
        String segundo = arquivoService.upload("usuarios", arquivo(bytesJpeg())).getPath();

        assertThat(primeiro).isNotEqualTo(segundo);
        verify(s3Client, times(2)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void upload_comUrlBaseTerminadaEmBarra_naoDeveDuplicarABarra() {
        ArquivoServiceImpl servico = new ArquivoServiceImpl(s3Client, BUCKET, URL_BASE + "/");

        ArquivoUploadOutputDto output = servico.upload("usuarios", arquivo(bytesJpeg()));

        assertThat(output.getUrl()).isEqualTo(URL_BASE + output.getPath());
    }

    @Test
    void upload_semUrlBaseConfigurada_deveRetornarSomenteOPath() {
        ArquivoServiceImpl servico = new ArquivoServiceImpl(s3Client, BUCKET, "");

        ArquivoUploadOutputDto output = servico.upload("usuarios", arquivo(bytesJpeg()));

        assertThat(output.getPath()).startsWith("/usuarios/");
        assertThat(output.getUrl()).isNull();
    }

    @Test
    void upload_semBucketConfigurado_deveLancarUploadArquivoException() {
        ArquivoServiceImpl servico = new ArquivoServiceImpl(s3Client, "", URL_BASE);

        assertThatThrownBy(() -> servico.upload("usuarios", arquivo(bytesJpeg())))
                .isInstanceOf(UploadArquivoException.class);

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void upload_quandoS3Falha_deveLancarUploadArquivoException() {
        doThrow(SdkClientException.create("sem conexão"))
                .when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        assertThatThrownBy(() -> arquivoService.upload("usuarios", arquivo(bytesJpeg())))
                .isInstanceOf(UploadArquivoException.class)
                .hasMessage("Não foi possível enviar o arquivo.");
    }
}

package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.output.ArquivoUploadOutputDto;
import br.com.toquerendo.enums.PastaArquivoEnum;
import br.com.toquerendo.exception.PastaArquivoNaoPermitidaException;
import br.com.toquerendo.exception.RuntimeApiException;
import br.com.toquerendo.exception.UploadArquivoException;
import br.com.toquerendo.service.ArquivoService;
import br.com.toquerendo.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
public class ArquivoServiceImpl implements ArquivoService {

    private static final int TAMANHO_CABECALHO_BYTES = 12;

    private static final String CACHE_CONTROL = "public, max-age=31536000, immutable";

    private record TipoImagem(String contentType, String extensao) {
    }

    private static final TipoImagem JPEG = new TipoImagem("image/jpeg", "jpeg");
    private static final TipoImagem PNG = new TipoImagem("image/png", "png");
    private static final TipoImagem WEBP = new TipoImagem("image/webp", "webp");

    private final S3Client s3Client;

    private final String bucket;

    private final String urlBasePublica;

    public ArquivoServiceImpl(S3Client s3Client,
                              @Value("${aws.s3.bucket}") String bucket,
                              @Value("${aws.s3.public-base-url}") String urlBasePublica) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.urlBasePublica = urlBasePublica;
    }

    public ArquivoUploadOutputDto upload(String pasta, MultipartFile arquivo) {
        PastaArquivoEnum pastaDestino = PastaArquivoEnum.fromNome(pasta);
        validarPermissao(pastaDestino);

        if (arquivo == null || arquivo.isEmpty()) {
            throw new RuntimeApiException("O arquivo é obrigatório e não pode estar vazio.");
        }
        if (bucket == null || bucket.isBlank()) {
            log.error("Upload recusado: a propriedade aws.s3.bucket (variável S3_BUCKET) não está configurada.");
            throw new UploadArquivoException();
        }

        TipoImagem tipo = identificarTipo(arquivo);
        String chave = pastaDestino.getNome() + "/" + UUID.randomUUID() + "." + tipo.extensao();

        PutObjectRequest requisicao = PutObjectRequest.builder()
                .bucket(bucket)
                .key(chave)
                .contentType(tipo.contentType())
                .cacheControl(CACHE_CONTROL)
                .build();

        try (InputStream conteudo = arquivo.getInputStream()) {
            s3Client.putObject(requisicao, RequestBody.fromInputStream(conteudo, arquivo.getSize()));
        } catch (IOException | SdkException e) {
            log.error("Falha ao enviar o arquivo {} para o bucket {}.", chave, bucket, e);
            throw new UploadArquivoException();
        }

        String path = "/" + chave;
        return ArquivoUploadOutputDto.builder()
                .path(path)
                .url(montarUrl(path))
                .build();
    }

    private void validarPermissao(PastaArquivoEnum pasta) {
        if (pasta.getRoleExigida() != null && !SecurityUtils.usuarioPossuiRole(pasta.getRoleExigida())) {
            throw new PastaArquivoNaoPermitidaException();
        }
    }

    /** Identifica o tipo pelos primeiros bytes do arquivo; o Content-Type e o nome enviados pelo cliente são ignorados. */
    private TipoImagem identificarTipo(MultipartFile arquivo) {
        byte[] cabecalho;
        try (InputStream conteudo = arquivo.getInputStream()) {
            cabecalho = conteudo.readNBytes(TAMANHO_CABECALHO_BYTES);
        } catch (IOException e) {
            log.error("Falha ao ler o arquivo enviado.", e);
            throw new UploadArquivoException();
        }

        if (ehJpeg(cabecalho)) return JPEG;
        if (ehPng(cabecalho)) return PNG;
        if (ehWebp(cabecalho)) return WEBP;

        throw new RuntimeApiException("Tipo de arquivo não suportado. Envie uma imagem JPEG, PNG ou WEBP.");
    }

    private boolean ehJpeg(byte[] b) {
        return b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF;
    }

    private boolean ehPng(byte[] b) {
        return b.length >= 8
                && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G'
                && b[4] == 0x0D && b[5] == 0x0A && b[6] == 0x1A && b[7] == 0x0A;
    }

    private boolean ehWebp(byte[] b) {
        return b.length >= 12
                && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P';
    }

    private String montarUrl(String path) {
        if (urlBasePublica == null || urlBasePublica.isBlank()) {
            return null;
        }
        String base = urlBasePublica.trim();
        return (base.endsWith("/") ? base.substring(0, base.length() - 1) : base) + path;
    }
}

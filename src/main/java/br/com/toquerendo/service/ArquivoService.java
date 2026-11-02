package br.com.toquerendo.service;

import br.com.toquerendo.dto.output.ArquivoUploadOutputDto;
import org.springframework.web.multipart.MultipartFile;

public interface ArquivoService {

    ArquivoUploadOutputDto upload(String pasta, MultipartFile arquivo);
}

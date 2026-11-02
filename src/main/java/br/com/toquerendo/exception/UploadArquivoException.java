package br.com.toquerendo.exception;

public class UploadArquivoException extends RuntimeException {
    public UploadArquivoException() {
        super("Não foi possível enviar o arquivo.");
    }
}

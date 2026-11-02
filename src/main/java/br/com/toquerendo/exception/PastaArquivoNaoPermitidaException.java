package br.com.toquerendo.exception;

public class PastaArquivoNaoPermitidaException extends RuntimeException {
    public PastaArquivoNaoPermitidaException() {
        super("Você não tem permissão para enviar arquivos para esta pasta.");
    }
}

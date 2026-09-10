package br.com.toquerendo.exception;

public class ProdutoNaoPertenceAoVendedorException extends RuntimeException {
    public ProdutoNaoPertenceAoVendedorException() {
        super("Você só pode alterar produtos associados à sua própria conta de vendedor.");
    }
}

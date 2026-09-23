package br.com.toquerendo.exception;

public class ProdutoNaoEncontradoException extends RuntimeException {
    public ProdutoNaoEncontradoException() {
        super("Produto base não encontrado.");
    }
}

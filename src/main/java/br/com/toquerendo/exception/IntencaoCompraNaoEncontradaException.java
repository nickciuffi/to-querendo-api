package br.com.toquerendo.exception;

public class IntencaoCompraNaoEncontradaException extends RuntimeException {
    public IntencaoCompraNaoEncontradaException() {
        super("Intenção de compra ativa não encontrada para o produto base informado.");
    }
}

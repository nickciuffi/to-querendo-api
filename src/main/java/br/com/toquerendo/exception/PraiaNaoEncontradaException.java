package br.com.toquerendo.exception;

public class PraiaNaoEncontradaException extends RuntimeException {
    public PraiaNaoEncontradaException() {
        super("Praia não encontrada para os critérios informados.");
    }
}

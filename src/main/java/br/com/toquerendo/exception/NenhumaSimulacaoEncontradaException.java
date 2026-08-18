package br.com.toquerendo.exception;

public class NenhumaSimulacaoEncontradaException extends RuntimeException {
    public NenhumaSimulacaoEncontradaException() {
        super("Nenhuma simulação encontrada!");
    }
}

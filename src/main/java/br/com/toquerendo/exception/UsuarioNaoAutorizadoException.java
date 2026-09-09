package br.com.toquerendo.exception;

public class UsuarioNaoAutorizadoException extends RuntimeException {
    public UsuarioNaoAutorizadoException() {
        super("Você só pode alterar a sua própria conta.");
    }
}

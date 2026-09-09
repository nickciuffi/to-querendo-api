package br.com.toquerendo.exception;

public class VendedorJaCadastradoException extends RuntimeException {
    public VendedorJaCadastradoException() {
        super("Este usuário já está cadastrado como vendedor.");
    }
}

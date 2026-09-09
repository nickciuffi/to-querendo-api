package br.com.toquerendo.exception;

public class CpfJaCadastradoException extends RuntimeException {
    public CpfJaCadastradoException() {
        super("Já existe um usuário cadastrado com esse cpf.");
    }
}

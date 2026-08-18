package br.com.toquerendo.exception;

public class ConversaoJsonException extends RuntimeException {
    public ConversaoJsonException() {
        super("Erro ao fazer a conversão para JSON");
    }
}

package br.com.toquerendo.exception;

public class EventSenderNaoInicializado extends RuntimeException {
    public EventSenderNaoInicializado() {
        super("O EventSender ainda não foi inicializado, evecute o metodo inicializar()");
    }
}

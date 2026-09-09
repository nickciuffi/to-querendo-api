package br.com.toquerendo.exception;

import br.com.toquerendo.dto.ApiResponse;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
    Classe handler de exceptions. Centraliza em apenas uma classe todo o tratamento de erros da aplicação.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ResponseEntity<ApiResponse<Object>> handleProdutoNaoEncontradoException(ProdutoNaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
    }

    @ExceptionHandler(NenhumaSimulacaoEncontradaException.class)
    public ResponseEntity<ApiResponse<Object>> handleProdutoNaoEncontradoException(NenhumaSimulacaoEncontradaException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
    }

    @ExceptionHandler(CriacaoVendedorException.class)
    public ResponseEntity<ApiResponse<Object>> handleCriacaoVendedorException(CriacaoVendedorException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(e.getMessage()));
    }

    @ExceptionHandler(TelemetriaNaoEncontradaException.class)
    public ResponseEntity<ApiResponse<Object>> handleProdutoNaoEncontradoException(TelemetriaNaoEncontradaException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
    }

    @ExceptionHandler(EventSenderNaoInicializado.class)
    public ResponseEntity<ApiResponse<Object>> handleEventSenderNaoInicializadoException(EventSenderNaoInicializado e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(e.getMessage()));
    }

    @ExceptionHandler(ConversaoJsonException.class)
    public ResponseEntity<ApiResponse<Object>> handleConversaoJsonException(ConversaoJsonException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(e.getMessage()));
    }

    @ExceptionHandler(EnviarEventSenderException.class)
    public ResponseEntity<ApiResponse<Object>> handleEnviarEventSenderException(EnviarEventSenderException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(e.getMessage()));
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ApiResponse<Object>> handleCredenciaisInvalidasException(CredenciaisInvalidasException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(e.getMessage()));
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailJaCadastradoException(EmailJaCadastradoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(e.getMessage()));
    }

    @ExceptionHandler(CpfJaCadastradoException.class)
    public ResponseEntity<ApiResponse<Object>> handleCpfJaCadastradoException(CpfJaCadastradoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(e.getMessage()));
    }

    @ExceptionHandler(UsuarioNaoAutorizadoException.class)
    public ResponseEntity<ApiResponse<Object>> handleUsuarioNaoAutorizadoException(UsuarioNaoAutorizadoException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(e.getMessage()));
    }

    @ExceptionHandler(VendedorJaCadastradoException.class)
    public ResponseEntity<ApiResponse<Object>> handleVendedorJaCadastradoException(VendedorJaCadastradoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> mensagens = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toCollection(ArrayList::new));
        mensagens.add(0, "Existem erros nos parâmetros enviados!");
        return ResponseEntity.badRequest().body(new ApiResponse<>(mensagens));
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiResponse<Object>> handleDateTimeParseException(DateTimeParseException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>("Ocorreu um erro com a data: " + e.getParsedString()));
    }

    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<ApiResponse<Object>> handleDBException(InvalidDataAccessResourceUsageException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>("Erro na consulta ao banco de dados"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFoundException(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Endpoint não encontrado: " + e.getResourcePath()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>("Erro interno do servidor."));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>("Endpoint somente disponível para usuários autorizados."));
    }

    @ExceptionHandler(RuntimeApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeApiException(RuntimeApiException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(e.getMessage()));
    }

}

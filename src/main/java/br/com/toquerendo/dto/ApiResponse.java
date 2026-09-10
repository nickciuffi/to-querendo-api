package br.com.toquerendo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Envelope padrão das respostas da API")
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@Data
public class ApiResponse<T> {

    @Schema(description = "Dados retornados pela requisição, quando aplicável")
    private T response;

    @Schema(description = "Mensagens de sucesso, erro ou validação retornadas pela requisição")
    private List<String> messages = new ArrayList<>();

    public ApiResponse(T response, String message) {
        this.response = response;
        this.messages.add(message);
    }

    public ApiResponse(String message) {
        this.messages.add(message);
    }

    public ApiResponse(List<String> message) {
        this.messages = message;
    }
}

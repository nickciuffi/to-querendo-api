package br.com.toquerendo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
@Data
public class ApiResponse<T> {

    private T response;

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

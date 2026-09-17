package br.com.toquerendo.dto;

import lombok.Data;

import java.util.List;

@Data
public class ApiUser {
    private String email;
    private List<String> roles;
}

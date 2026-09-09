package br.com.toquerendo.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AtualizarProdutoBaseInputDto {

    private String nome;

    private String descricao;

    private String urlFoto;

    private BigDecimal precoMinimo;

    private Boolean estaAtivo;
}

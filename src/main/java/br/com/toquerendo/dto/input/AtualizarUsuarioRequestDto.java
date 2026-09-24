package br.com.toquerendo.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AtualizarUsuarioRequestDto {

    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    private String nome;

    @Schema(description = "Telefone de contato do usuário", example = "11999999999")
    private String telefone;

    @Schema(description = "CPF do usuário, apenas dígitos", example = "12345678900")
    @Pattern(regexp = "\\d{11}", message = "cpf deve conter 11 dígitos numéricos")
    private String cpf;

    @Schema(description = "URL da foto de perfil do usuário", example = "https://exemplo.com/foto.jpg")
    private String urlFoto;

    @Schema(description = "ID da praia a qual o usuário está associado", example = "1")
    private Long idPraia;

    @Schema(description = "Descrição do negócio do vendedor", example = "Vendedor de picolés")
    private String descricao;

    @Schema(description = "indica se um vendedor está online", example = "true")
    private boolean online;
}

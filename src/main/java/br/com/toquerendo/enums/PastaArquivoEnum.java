package br.com.toquerendo.enums;

import br.com.toquerendo.exception.RuntimeApiException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum PastaArquivoEnum {

    USUARIOS("usuarios", null),
    INTENCOES_COMPRA("intencoes-compra", null),
    PRODUTOS_ESPECIFICOS("produtos-especificos", "ROLE_VENDEDOR"),
    PRODUTOS_BASE("produtos-base", "ROLE_ADMIN"),
    PRAIAS("praias", "ROLE_ADMIN");

    private final String nome;

    /** Role exigida para enviar arquivos para a pasta; {@code null} libera qualquer usuário autenticado. */
    private final String roleExigida;

    public static PastaArquivoEnum fromNome(String nome) {
        String nomeNormalizado = nome == null ? "" : nome.trim();
        return Arrays.stream(values())
                .filter(pasta -> pasta.nome.equalsIgnoreCase(nomeNormalizado))
                .findFirst()
                .orElseThrow(() -> new RuntimeApiException("Pasta inválida. Valores aceitos: "
                        + Arrays.stream(values()).map(PastaArquivoEnum::getNome).collect(Collectors.joining(", ")) + "."));
    }
}

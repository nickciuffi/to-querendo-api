package br.com.toquerendo.utils;

public class DocumentoUtils {

    private static final int TAMANHO_CPF = 11;

    public static boolean isCpfValido(String cpf) {
        if (cpf == null) {
            return false;
        }

        String cpfLimpo = cpf.replaceAll("[.\\-]", "");

        if (cpfLimpo.length() != TAMANHO_CPF || !cpfLimpo.matches("\\d+")) {
            return false;
        }

        if (cpfLimpo.chars().distinct().count() == 1) {
            return false;
        }

        int[] digitos = cpfLimpo.chars().map(c -> c - '0').toArray();

        if (calcularDigitoVerificador(digitos, 9) != digitos[9]) {
            return false;
        }

        return calcularDigitoVerificador(digitos, 10) == digitos[10];
    }

    private static int calcularDigitoVerificador(int[] digitos, int quantidadeDigitos) {
        int peso = quantidadeDigitos + 1;
        int soma = 0;

        for (int i = 0; i < quantidadeDigitos; i++) {
            soma += digitos[i] * peso;
            peso--;
        }

        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}

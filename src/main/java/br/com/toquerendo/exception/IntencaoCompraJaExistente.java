package br.com.toquerendo.exception;

public class IntencaoCompraJaExistente extends RuntimeException {
    public IntencaoCompraJaExistente(String produto) {
        super("Intenção de compra já existente para o produto: " + produto);
    }
}

package br.com.toquerendo.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoVendedorId implements Serializable {

    private Long idPagamento;

    private Long idVendedor;
}

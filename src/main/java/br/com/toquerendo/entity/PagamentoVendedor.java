package br.com.toquerendo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pagamentos_vendedores")
@Data
public class PagamentoVendedor {

    @EmbeddedId
    private PagamentoVendedorId id = new PagamentoVendedorId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idPagamento")
    @JoinColumn(name = "id_pagamento")
    private PagamentoAceito pagamentoAceito;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idVendedor")
    @JoinColumn(name = "id_vendedor")
    private Vendedor vendedor;
}

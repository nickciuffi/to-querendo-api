package br.com.toquerendo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "produtos_base")
@Data
public class ProdutoBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(name = "url_foto")
    private String urlFoto;

    @Column(name = "preco_minimo", precision = 10, scale = 2)
    private BigDecimal precoMinimo;

    @Column(name = "esta_ativo")
    private Boolean estaAtivo;
}

package br.com.toquerendo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produtos_especificos")
@Data
public class ProdutoEspecifico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(name = "url_foto")
    private String urlFoto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_produto_base", nullable = false)
    private ProdutoBase produtoBase;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_vendedor", nullable = false)
    private Vendedor vendedor;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "produto_ativo", nullable = false)
    private Boolean produtoAtivo;

    @Column(name = "ts_criacao_produto", nullable = false)
    private LocalDateTime tsCriacaoProduto;
}

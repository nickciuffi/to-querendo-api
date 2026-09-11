package br.com.toquerendo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "intencoes_compra")
@Data
public class IntencaoCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto_base", nullable = false)
    private ProdutoBase produtoBase;

    @Column(name = "descricao_local")
    private String descricaoLocal;

    private String observacoes;

    @Column(name = "url_foto_local")
    private String urlFotoLocal;

    @Column(name = "ts_criacao_intencao", nullable = false)
    private LocalDateTime tsCriacaoIntencao;

    @Column(name = "ts_conclusao_intencao")
    private LocalDateTime tsConclusaoIntencao;

    @Column(name = "esta_ativo", nullable = false)
    private Boolean estaAtivo;
}

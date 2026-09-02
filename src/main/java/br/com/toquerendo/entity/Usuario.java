package br.com.toquerendo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String senha;

    private String telefone;

    @Column(unique = true, length = 11)
    private String cpf;

    @Column(name = "url_foto")
    private String urlFoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_praia")
    private Praia praia;

    @Column(name = "ts_criacao_conta", nullable = false)
    private LocalDateTime tsCriacaoConta;

    @Column(name = "conta_ativa", nullable = false)
    private Boolean contaAtiva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    public void setCategoriaId(Long categoriaId) {
        if (categoriaId == null) {
            this.categoria = null;
            return;
        }

        Categoria categoriaReferencia = new Categoria();
        categoriaReferencia.setId(categoriaId);
        this.categoria = categoriaReferencia;
    }
}

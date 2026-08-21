package br.com.toquerendo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "vendedores")
@Data
public class Vendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    private String descricao;

    @Column(nullable = false)
    private Boolean online;
}

package br.com.toquerendo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pagamentos_aceitos")
@Data
public class PagamentoAceito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;
}

package br.com.toquerendo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "praias")
@Data
public class Praia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "url_foto")
    private String urlFoto;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String estado;
}

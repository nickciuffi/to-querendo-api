package br.com.toquerendo.entity.local;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "TB02_PARCELAS")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Parcela {

    @Id
    @Column(name = "ID_PARCELA")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idParcela;

    @Column(name = "NU_PARCELA")
    private Integer numeroParcela;

    @Column(name = "VR_AMORTIZACAO")
    private BigDecimal valorAmortizacao;

    @Column(name = "VR_JUROS")
    private BigDecimal valorJuros;

    @Column(name = "VR_PRESTACAO")
    private BigDecimal valorPrestacao;

}

package br.com.toquerendo.repository;

import br.com.toquerendo.entity.IntencaoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IntencaoCompraRepository extends JpaRepository<IntencaoCompra, Long> {

    @Query("select ic from IntencaoCompra ic " +
            "join fetch ic.usuario u " +
            "join fetch ic.produtoBase pb " +
            "where u.categoria.id = :idCategoriaTurista " +
            "and u.praia.id = :idPraia " +
            "and ic.produtoBase.id in :idsProdutoBase " +
            "and ic.estaAtivo = true")
    List<IntencaoCompra> findAllAtivasDeTuristasPorPraiaEProdutosBase(
            @Param("idCategoriaTurista") Integer idCategoriaTurista,
            @Param("idPraia") Long idPraia,
            @Param("idsProdutoBase") List<Long> idsProdutoBase);
}

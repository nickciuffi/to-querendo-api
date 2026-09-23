package br.com.toquerendo.repository;

import br.com.toquerendo.entity.ProdutoEspecifico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProdutoEspecificoRepository extends JpaRepository<ProdutoEspecifico, Long> {
    Integer countByVendedorIdAndProdutoAtivoTrue(Long vendedorId);
    Integer countByVendedorId(Long vendedorId);

    @Query("select pe from ProdutoEspecifico pe " +
            "where pe.vendedor.id = :vendedorId " +
            "and pe.produtoAtivo = true " +
            "and pe.produtoBase.estaAtivo = true")
    List<ProdutoEspecifico> findAllByVendedorIdAndProdutoAtivoTrueAndProdutoBaseAtivo(@Param("vendedorId") Long vendedorId);

    List<ProdutoEspecifico> findAllByVendedorIdAndProdutoAtivoTrue(@Param("vendedorId") Long vendedorId);

    @Query("select pe from ProdutoEspecifico pe " +
            "where pe.vendedor.id = :vendedorId " +
            "and pe.produtoBase.id = :idProdutoBase " +
            "and pe.produtoAtivo = true " +
            "and pe.produtoBase.estaAtivo = true")
    List<ProdutoEspecifico> findAllByVendedorIdAndProdutoBaseIdAndProdutoAtivoTrueAndProdutoBaseAtivo(
            @Param("vendedorId") Long vendedorId,
            @Param("idProdutoBase") Long idProdutoBase);

    List<ProdutoEspecifico> findAllByVendedorId(@Param("vendedorId") Long vendedorId);
}

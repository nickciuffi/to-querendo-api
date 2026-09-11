package br.com.toquerendo.repository;

import br.com.toquerendo.entity.ProdutoEspecifico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProdutoEspecificoRepository extends JpaRepository<ProdutoEspecifico, Long> {
    Integer countByVendedorIdAndProdutoAtivoTrue(Long vendedorId);
    Integer countByVendedorId(Long vendedorId);
    List<ProdutoEspecifico> findAllByVendedorIdAndProdutoAtivoTrue(Long vendedorId);

    @Query("select pe from ProdutoEspecifico pe " +
            "where pe.produtoBase.id = :idProdutoBase " +
            "and pe.produtoAtivo = true " +
            "and pe.vendedor.online = true " +
            "and pe.vendedor.usuario.praia.id = :idPraia")
    List<ProdutoEspecifico> findAllComVendedorOnlinePorProdutoBaseEPraia(
            @Param("idProdutoBase") Long idProdutoBase,
            @Param("idPraia") Long idPraia);
}

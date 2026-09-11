package br.com.toquerendo.repository;

import br.com.toquerendo.entity.ProdutoBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProdutoBaseRepository extends JpaRepository<ProdutoBase, Long> {

    List<ProdutoBase> findAllByEstaAtivoTrue();

    Optional<ProdutoBase> findByIdAndEstaAtivoTrue(Long id);

    @Query("select distinct pe.produtoBase from ProdutoEspecifico pe " +
            "where pe.vendedor.usuario.praia.id = :idPraia " +
            "and pe.produtoAtivo = true " +
            "and pe.produtoBase.estaAtivo = true")
    List<ProdutoBase> findAllByEstaAtivoTrueAndIdPraia(@Param("idPraia") Integer idPraia);
}

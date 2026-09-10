package br.com.toquerendo.repository;

import br.com.toquerendo.entity.ProdutoEspecifico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoEspecificoRepository extends JpaRepository<ProdutoEspecifico, Long> {
    Integer countByVendedorIdAndProdutoAtivoTrue(Long vendedorId);
    Integer countByVendedorId(Long vendedorId);
    List<ProdutoEspecifico> findAllByVendedorIdAndProdutoAtivoTrue(Long vendedorId);
}

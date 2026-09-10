package br.com.toquerendo.repository;

import br.com.toquerendo.entity.ProdutoEspecifico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoEspecificoRepository extends JpaRepository<ProdutoEspecifico, Long> {
    public Integer countByIdVendedor(Long idVendedor);
}

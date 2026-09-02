package br.com.toquerendo.repository;

import br.com.toquerendo.entity.ProdutoBase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoBaseRepository extends JpaRepository<ProdutoBase, Long> {

    List<ProdutoBase> findAllByEstaAtivoTrue();
}

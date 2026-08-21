package br.com.toquerendo.repository;

import br.com.toquerendo.entity.ProdutoBase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoBaseRepository extends JpaRepository<ProdutoBase, Long> {
}

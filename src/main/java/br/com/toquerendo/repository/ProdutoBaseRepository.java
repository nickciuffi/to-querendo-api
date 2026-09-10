package br.com.toquerendo.repository;

import br.com.toquerendo.entity.ProdutoBase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProdutoBaseRepository extends JpaRepository<ProdutoBase, Long> {

    List<ProdutoBase> findAllByEstaAtivoTrue();

    Optional<ProdutoBase> findByIdAndEstaAtivoTrue(Long id);
}

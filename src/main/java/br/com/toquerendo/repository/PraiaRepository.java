package br.com.toquerendo.repository;

import br.com.toquerendo.entity.Praia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PraiaRepository extends JpaRepository<Praia, Long> {
}

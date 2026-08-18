package br.com.toquerendo.repository.local;

import br.com.toquerendo.entity.local.Parcela;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParcelaRepository extends JpaRepository<Parcela, Long> {
}

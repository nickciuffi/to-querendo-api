package br.com.toquerendo.repository;

import br.com.toquerendo.entity.Praia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PraiaRepository extends JpaRepository<Praia, Long> {

    List<Praia> findAllByLatitudeIsNotNullAndLongitudeIsNotNull();
}

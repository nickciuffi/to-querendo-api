package br.com.toquerendo.repository;

import br.com.toquerendo.entity.Localizacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalizacaoRepository extends JpaRepository<Localizacao, Long> {
}

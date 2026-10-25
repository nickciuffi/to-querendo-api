package br.com.toquerendo.repository;

import br.com.toquerendo.entity.Localizacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocalizacaoRepository extends JpaRepository<Localizacao, Long> {

    Optional<Localizacao> findByUsuarioEmail(String email);
}

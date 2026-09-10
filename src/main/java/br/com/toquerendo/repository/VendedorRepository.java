package br.com.toquerendo.repository;

import br.com.toquerendo.entity.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VendedorRepository extends JpaRepository<Vendedor, Long> {
    boolean existsByUsuarioEmail(String email);

    @Query("select count(v) from Vendedor v " +
            "where v.usuario.praia.id = :idPraia " +
            "and v.online = true")
    Integer countVendedoresPorPraia(@Param("idPraia") Long idPraia);

    Optional<Vendedor> findByUsuarioEmail(String email);

}

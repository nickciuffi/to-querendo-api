package br.com.toquerendo.repository;

import br.com.toquerendo.entity.PagamentoVendedor;
import br.com.toquerendo.entity.PagamentoVendedorId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoVendedorRepository extends JpaRepository<PagamentoVendedor, PagamentoVendedorId> {
}

package br.com.toquerendo.service;

import br.com.toquerendo.dto.input.AtualizarProdutoEspecificoInputDto;
import br.com.toquerendo.dto.input.CriarProdutoEspecificoInputDto;
import br.com.toquerendo.dto.output.ProdutoEspecificoOutputDto;

import java.util.List;

public interface ProdutoEspecificoService {

    ProdutoEspecificoOutputDto criarProdutoEspecifico(CriarProdutoEspecificoInputDto input);

    ProdutoEspecificoOutputDto atualizarProdutoEspecifico(Long id, AtualizarProdutoEspecificoInputDto input);

    List<ProdutoEspecificoOutputDto> consultarProdutosDoVendedorLogado();

    ProdutoEspecificoOutputDto deletarProdutoEspecifico(Long id);
}

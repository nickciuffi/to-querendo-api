package br.com.toquerendo.service;

import br.com.toquerendo.dto.input.AtualizarProdutoBaseInputDto;
import br.com.toquerendo.dto.input.CriarProdutoBaseInputDto;
import br.com.toquerendo.dto.output.ProdutoBaseOutputDto;

import java.util.List;

public interface ProdutoBaseService {

    ProdutoBaseOutputDto criarProdutoBase(CriarProdutoBaseInputDto input);

    List<ProdutoBaseOutputDto> consultarProdutosBaseAtivos(Integer idPraia);

    List<ProdutoBaseOutputDto> consultarProdutosBase();

    void deletarProdutoBase(Long id);

    ProdutoBaseOutputDto atualizarProdutoBase(Long id, AtualizarProdutoBaseInputDto input);
}

package br.com.toquerendo.service;

import br.com.toquerendo.dto.input.CriarVendedorRequestDto;
import br.com.toquerendo.dto.output.VendedorLocalizacaoProdutosOutputDto;
import br.com.toquerendo.dto.output.VendedorOutputDto;

import java.util.List;

public interface VendedorService {

    VendedorOutputDto cadastrarVendedor(CriarVendedorRequestDto criarVendedorRequest);

    VendedorOutputDto obterDadosVendedor();

    List<VendedorLocalizacaoProdutosOutputDto> consultarLocalizacaoVendedores(Long idProdutoBase, Long idPraia);
}

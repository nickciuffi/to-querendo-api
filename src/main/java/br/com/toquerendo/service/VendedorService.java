package br.com.toquerendo.service;

import br.com.toquerendo.dto.input.CriarVendedorRequestDto;
import br.com.toquerendo.dto.output.UsuarioOutputDto;
import br.com.toquerendo.dto.output.VendedorLocalizacaoProdutosOutputDto;

import java.util.List;

public interface VendedorService {

    UsuarioOutputDto cadastrarVendedor(CriarVendedorRequestDto criarVendedorRequest);

    List<VendedorLocalizacaoProdutosOutputDto> consultarLocalizacaoVendedores(Long idProdutoBase, Long idPraia);
}

package br.com.toquerendo.service;

import br.com.toquerendo.dto.input.CriarVendedorRequestDto;
import br.com.toquerendo.dto.output.VendedorLocalizacaoOutputDto;
import br.com.toquerendo.dto.output.VendedorOutputDto;

import java.util.List;

public interface VendedorService {

    VendedorOutputDto cadastrarVendedor(CriarVendedorRequestDto criarVendedorRequest);

    VendedorOutputDto obterDadosVendedor();

    List<VendedorLocalizacaoOutputDto> consultarLocalizacaoVendedores(Long idProdutoBase, Long idPraia);
}

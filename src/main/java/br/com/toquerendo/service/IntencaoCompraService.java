package br.com.toquerendo.service;

import br.com.toquerendo.dto.input.CriarIntencaoCompraInputDto;
import br.com.toquerendo.dto.output.BanhistaComIntencoesOutputDto;
import br.com.toquerendo.dto.output.IntencaoCompraOutputDto;

import java.util.List;

public interface IntencaoCompraService {

    IntencaoCompraOutputDto criarIntencaoCompra(CriarIntencaoCompraInputDto input);

    List<BanhistaComIntencoesOutputDto> consultarBanhistasComIntencaoDeCompra();

    List<IntencaoCompraOutputDto> consultarIntencoesCompraBanhista();
}

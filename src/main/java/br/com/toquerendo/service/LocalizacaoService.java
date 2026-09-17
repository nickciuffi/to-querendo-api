package br.com.toquerendo.service;

import br.com.toquerendo.dto.input.AtualizarLocalizacaoInputDto;
import br.com.toquerendo.dto.output.LocalizacaoOutputDto;

public interface LocalizacaoService {

    LocalizacaoOutputDto atualizarLocalizacao(AtualizarLocalizacaoInputDto input);
}

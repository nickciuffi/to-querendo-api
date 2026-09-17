package br.com.toquerendo.service;

import br.com.toquerendo.dto.input.AtualizarPraiaInputDto;
import br.com.toquerendo.dto.input.CriarPraiaInputDto;
import br.com.toquerendo.dto.output.PraiaOutputDto;

import java.util.List;

public interface PraiaService {

    PraiaOutputDto criarPraia(CriarPraiaInputDto input);

    List<PraiaOutputDto> consultarPraias();

    PraiaOutputDto consultarPraiaPorId(Long id);

    PraiaOutputDto atualizarPraia(Long id, AtualizarPraiaInputDto input);

    void deletarPraia(Long id);
}

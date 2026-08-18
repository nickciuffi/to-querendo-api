package br.com.toquerendo.service;

import br.com.toquerendo.dto.TesteDto;

import java.util.List;

public interface SimulacaoService {

    public TesteDto realizarSimulacao(TesteDto req);

    public List<TesteDto> consultarSimulacoes(TesteDto req);

}

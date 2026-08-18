package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.TesteDto;
import br.com.toquerendo.service.SimulacaoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SimulacaoServiceImpl implements SimulacaoService {

    public TesteDto realizarSimulacao(TesteDto req){

        return null;
    }

    public List<TesteDto> consultarSimulacoes(TesteDto req){
       return null;
    }
}

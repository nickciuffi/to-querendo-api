package br.com.toquerendo.controller;

import br.com.toquerendo.dto.ApiResponse;
import br.com.toquerendo.dto.PaginacaoDTO;
import br.com.toquerendo.dto.TesteDto;
import br.com.toquerendo.service.SimulacaoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.aspectj.weaver.ast.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/simulacao")
@AllArgsConstructor
public class SimulacaoController {

    private SimulacaoService simulacaoService;

    @PostMapping("/realizar")
    public ResponseEntity<ApiResponse<TesteDto>> realizarSimulacao(@RequestBody @Valid TesteDto req){
        TesteDto res = simulacaoService.realizarSimulacao(req);
        return ResponseEntity.ok().body(new ApiResponse<TesteDto>(res, "Simulação realizada com sucesso!"));
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<TesteDto>> consultarSimulacoesComPaginacao(@Valid @ModelAttribute TesteDto req){
        List<TesteDto> res = simulacaoService.consultarSimulacoes(req);
        return ResponseEntity.ok()
                .body(new ApiResponse<>(
                        res.get(0), "Sucesso"
                ));
    }

}

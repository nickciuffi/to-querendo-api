package br.com.toquerendo.controller;

import br.com.toquerendo.dto.ApiResponse;
import br.com.toquerendo.dto.output.ProdutoOutputDto;
import br.com.toquerendo.entity.ProdutoBase;
import br.com.toquerendo.repository.ProdutoBaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/produto")
@AllArgsConstructor
public class ProdutoController {

    private ProdutoBaseRepository produtoBaseRepository;

    @GetMapping("")
    ResponseEntity<ApiResponse<List<ProdutoOutputDto>>> consultarProdutosDisponiveis(){
        List<ProdutoBase> prodsEnt = produtoBaseRepository.findAll();
        List<ProdutoOutputDto> prodsOutput = new ArrayList<>();
        for(ProdutoBase prodEnt : prodsEnt) {
            ProdutoOutputDto prodOutput = ProdutoOutputDto
                    .builder()
                    .nomeProduto(prodEnt.getNome())
                    .preco("R$" + prodEnt.getPrecoMinimo())
                    .build();
            prodsOutput.add(prodOutput);
        }
        return ResponseEntity.ok().body(new ApiResponse(prodsOutput, "Produtos consultados com sucesso!"));
    }

    @PostMapping("")
    ResponseEntity<ApiResponse<ProdutoOutputDto>> criarProdutoBase(@RequestBody ProdutoBase produtoBase) {
        ProdutoBase prodSalvo = produtoBaseRepository.save(produtoBase);
        ProdutoOutputDto prodOutput = ProdutoOutputDto
                .builder()
                .nomeProduto(prodSalvo.getNome())
                .preco("R$" + prodSalvo.getPrecoMinimo())
                .build();
        return ResponseEntity.ok().body(new ApiResponse(prodOutput, "Produto criado com sucesso!"));
    }
}

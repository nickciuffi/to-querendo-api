package br.com.toquerendo.controller;

import br.com.toquerendo.dto.ApiResponse;
import br.com.toquerendo.dto.input.CriarProdutoBaseInputDto;
import br.com.toquerendo.dto.output.ProdutoBaseOutputDto;
import br.com.toquerendo.service.implementation.ProdutoBaseServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/produto")
@AllArgsConstructor
public class ProdutoController {

    private final ProdutoBaseServiceImpl produtoBaseService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<ProdutoBaseOutputDto>>> consultarProdutosDisponiveis() {
        List<ProdutoBaseOutputDto> prodsOutput = produtoBaseService.consultarProdutosBaseAtivos();
        return ResponseEntity.ok().body(new ApiResponse<>(prodsOutput, "Produtos consultados com sucesso!"));
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<ProdutoBaseOutputDto>> criarProdutoBase(@RequestBody @Valid CriarProdutoBaseInputDto produtoBase) {
        ProdutoBaseOutputDto output = produtoBaseService.criarProdutoBase(produtoBase);

        return ResponseEntity.ok().body(new ApiResponse<>(output, "Produto criado com sucesso!"));
    }
}

package br.com.toquerendo.controller;

import br.com.toquerendo.dto.ApiResponse;
import br.com.toquerendo.dto.input.AtualizarProdutoBaseInputDto;
import br.com.toquerendo.dto.input.CriarProdutoBaseInputDto;
import br.com.toquerendo.dto.output.ProdutoBaseOutputDto;
import br.com.toquerendo.security.annotation.AdminOnly;
import br.com.toquerendo.service.implementation.ProdutoBaseServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/produto-base")
@AllArgsConstructor
public class ProdutoController {

    private final ProdutoBaseServiceImpl produtoBaseService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<ProdutoBaseOutputDto>>> consultarProdutosDisponiveis() {
        List<ProdutoBaseOutputDto> prodsOutput = produtoBaseService.consultarProdutosBaseAtivos();
        return ResponseEntity.ok().body(new ApiResponse<>(prodsOutput, "Produtos consultados com sucesso!"));
    }

    @GetMapping("/todos")
    @AdminOnly
    public ResponseEntity<ApiResponse<List<ProdutoBaseOutputDto>>> consultarTodosProdutos() {
        List<ProdutoBaseOutputDto> prodsOutput = produtoBaseService.consultarProdutosBase();
        return ResponseEntity.ok().body(new ApiResponse<>(prodsOutput, "Produtos consultados com sucesso!"));
    }

    @PostMapping("")
    @AdminOnly
    public ResponseEntity<ApiResponse<ProdutoBaseOutputDto>> criarProdutoBase(@RequestBody @Valid CriarProdutoBaseInputDto produtoBase) {
        ProdutoBaseOutputDto output = produtoBaseService.criarProdutoBase(produtoBase);

        return ResponseEntity.ok().body(new ApiResponse<>(output, "Produto criado com sucesso!"));
    }

    @DeleteMapping("/{id}")
    @AdminOnly
    public ResponseEntity<ApiResponse<Object>> deletarProdutoBase(@PathVariable Long id) {
        produtoBaseService.deletarProdutoBase(id);
        return ResponseEntity.ok().body(new ApiResponse<>("Produto removido com sucesso!"));
    }

    @PutMapping("/{id}")
    @AdminOnly
    public ResponseEntity<ApiResponse<ProdutoBaseOutputDto>> atualizarProdutoBase(@PathVariable Long id, @RequestBody AtualizarProdutoBaseInputDto produtoBase) {
        ProdutoBaseOutputDto output = produtoBaseService.atualizarProdutoBase(id, produtoBase);
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Produto atualizado com sucesso!"));
    }
}

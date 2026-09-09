package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.CriarProdutoBaseInputDto;
import br.com.toquerendo.dto.output.ProdutoBaseOutputDto;
import br.com.toquerendo.entity.ProdutoBase;
import br.com.toquerendo.repository.ProdutoBaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProdutoBaseServiceImpl {

    private final ProdutoBaseRepository produtoBaseRepository;

    public ProdutoBaseOutputDto criarProdutoBase(CriarProdutoBaseInputDto input) {
        ProdutoBase produtoBase = produtoBaseRepository.save(CriarProdutoBaseInputDto.toEntity(input));
        return ProdutoBaseOutputDto.fromEntity(produtoBase);
    }

    public List<ProdutoBaseOutputDto> consultarProdutosBaseAtivos() {
        return produtoBaseRepository.findAllByEstaAtivoTrue()
                .stream()
                .map(ProdutoBaseOutputDto::fromEntity)
                .toList();
    }
}

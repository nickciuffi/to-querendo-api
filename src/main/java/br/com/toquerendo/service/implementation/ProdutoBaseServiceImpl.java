package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.AtualizarProdutoBaseInputDto;
import br.com.toquerendo.dto.input.CriarProdutoBaseInputDto;
import br.com.toquerendo.dto.output.ProdutoBaseOutputDto;
import br.com.toquerendo.entity.ProdutoBase;
import br.com.toquerendo.exception.ProdutoNaoEncontradoException;
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

    public List<ProdutoBaseOutputDto> consultarProdutosBase() {
        return produtoBaseRepository.findAll()
                .stream()
                .map(ProdutoBaseOutputDto::fromEntity)
                .toList();
    }

    public void deletarProdutoBase(Long id) {
        ProdutoBase produtoBase = produtoBaseRepository.findById(id)
                .orElseThrow(ProdutoNaoEncontradoException::new);

        produtoBaseRepository.delete(produtoBase);
    }

    public ProdutoBaseOutputDto atualizarProdutoBase(Long id, AtualizarProdutoBaseInputDto input) {
        ProdutoBase produtoBase = produtoBaseRepository.findById(id)
                .orElseThrow(ProdutoNaoEncontradoException::new);

        produtoBase.setNome(input.getNome() != null ? input.getNome() : produtoBase.getNome());
        produtoBase.setDescricao(input.getDescricao() != null ? input.getDescricao() : produtoBase.getDescricao());
        produtoBase.setUrlFoto(input.getUrlFoto() != null ? input.getUrlFoto() : produtoBase.getUrlFoto());
        produtoBase.setPrecoMinimo(input.getPrecoMinimo() != null ? input.getPrecoMinimo() : produtoBase.getPrecoMinimo());
        produtoBase.setEstaAtivo(input.getEstaAtivo() != null ? input.getEstaAtivo() : produtoBase.getEstaAtivo());

        ProdutoBase atualizado = produtoBaseRepository.save(produtoBase);
        return ProdutoBaseOutputDto.fromEntity(atualizado);
    }
}

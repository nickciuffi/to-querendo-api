package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.AtualizarProdutoEspecificoInputDto;
import br.com.toquerendo.dto.input.CriarProdutoEspecificoInputDto;
import br.com.toquerendo.dto.output.ProdutoEspecificoOutputDto;
import br.com.toquerendo.entity.ProdutoBase;
import br.com.toquerendo.entity.ProdutoEspecifico;
import br.com.toquerendo.entity.Vendedor;
import br.com.toquerendo.exception.ProdutoNaoEncontradoException;
import br.com.toquerendo.exception.ProdutoNaoPertenceAoVendedorException;
import br.com.toquerendo.exception.RuntimeApiException;
import br.com.toquerendo.repository.ProdutoBaseRepository;
import br.com.toquerendo.repository.ProdutoEspecificoRepository;
import br.com.toquerendo.repository.VendedorRepository;
import br.com.toquerendo.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ProdutoEspecificoServiceImpl {

    private final ProdutoEspecificoRepository produtoEspecificoRepository;

    private final ProdutoBaseRepository produtoBaseRepository;

    private final VendedorRepository vendedorRepository;

    public ProdutoEspecificoOutputDto criarProdutoEspecifico(CriarProdutoEspecificoInputDto input) {
        Vendedor vendedor = buscarVendedorLogado();

        ProdutoBase produtoBase = produtoBaseRepository.findByIdAndEstaAtivoTrue(input.getIdProdutoBase())
                .orElseThrow(ProdutoNaoEncontradoException::new);

        if(input.getPreco().compareTo(produtoBase.getPrecoMinimo()) < 0){
            throw new RuntimeApiException("O preço do produto específico não pode ser menor que o preço mínimo do produto base.");
        }

        ProdutoEspecifico produtoEspecifico = new ProdutoEspecifico();
        produtoEspecifico.setNome(input.getNome());
        produtoEspecifico.setDescricao(input.getDescricao());
        produtoEspecifico.setUrlFoto(input.getUrlFoto());
        produtoEspecifico.setProdutoBase(produtoBase);
        produtoEspecifico.setVendedor(vendedor);
        produtoEspecifico.setPreco(input.getPreco());
        produtoEspecifico.setProdutoAtivo(true);
        produtoEspecifico.setTsCriacaoProduto(LocalDateTime.now());

        ProdutoEspecifico produtoSalvo = produtoEspecificoRepository.save(produtoEspecifico);
        return ProdutoEspecificoOutputDto.fromEntity(produtoSalvo);
    }

    public ProdutoEspecificoOutputDto atualizarProdutoEspecifico(Long id, AtualizarProdutoEspecificoInputDto input) {
        Vendedor vendedor = buscarVendedorLogado();

        ProdutoEspecifico produtoEspecifico = produtoEspecificoRepository.findById(id)
                .orElseThrow(ProdutoNaoEncontradoException::new);

        if (!Objects.equals(produtoEspecifico.getVendedor().getId(), vendedor.getId())) {
            throw new ProdutoNaoPertenceAoVendedorException();
        }

        if(input.getPreco().compareTo(produtoEspecifico.getProdutoBase().getPrecoMinimo()) < 0){
            throw new RuntimeApiException("O preço do produto específico não pode ser menor que o preço mínimo do produto base.");
        }

        produtoEspecifico.setNome(input.getNome() != null ? input.getNome() : produtoEspecifico.getNome());
        produtoEspecifico.setDescricao(input.getDescricao() != null ? input.getDescricao() : produtoEspecifico.getDescricao());
        produtoEspecifico.setUrlFoto(input.getUrlFoto() != null ? input.getUrlFoto() : produtoEspecifico.getUrlFoto());
        produtoEspecifico.setPreco(input.getPreco() != null ? input.getPreco() : produtoEspecifico.getPreco());
        produtoEspecifico.setProdutoAtivo(input.getProdutoAtivo() != null ? input.getProdutoAtivo() : produtoEspecifico.getProdutoAtivo());

        ProdutoEspecifico produtoAtualizado = produtoEspecificoRepository.save(produtoEspecifico);
        return ProdutoEspecificoOutputDto.fromEntity(produtoAtualizado);
    }

    public List<ProdutoEspecificoOutputDto> consultarProdutosAtivosDoVendedorLogado() {
        Vendedor vendedor = buscarVendedorLogado();

        return produtoEspecificoRepository.findAllByVendedorIdAndProdutoAtivoTrue(vendedor.getId())
                .stream()
                .map(ProdutoEspecificoOutputDto::fromEntity)
                .toList();
    }

    private Vendedor buscarVendedorLogado() {
        String email = SecurityUtils.getEmailUsuarioLogado();
        return vendedorRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new RuntimeException("Vendedor não encontrado."));
    }
}

package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.CriarIntencaoCompraInputDto;
import br.com.toquerendo.dto.output.IntencaoCompraOutputDto;
import br.com.toquerendo.entity.IntencaoCompra;
import br.com.toquerendo.entity.ProdutoBase;
import br.com.toquerendo.entity.Usuario;
import br.com.toquerendo.exception.ProdutoNaoEncontradoException;
import br.com.toquerendo.repository.IntencaoCompraRepository;
import br.com.toquerendo.repository.ProdutoBaseRepository;
import br.com.toquerendo.repository.UsuarioRepository;
import br.com.toquerendo.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class IntencaoCompraServiceImpl {

    private final IntencaoCompraRepository intencaoCompraRepository;

    private final ProdutoBaseRepository produtoBaseRepository;

    private final UsuarioRepository usuarioRepository;

    public IntencaoCompraOutputDto criarIntencaoCompra(CriarIntencaoCompraInputDto input) {
        Usuario usuario = buscarUsuarioLogado();

        ProdutoBase produtoBase = produtoBaseRepository.findByIdAndEstaAtivoTrue(input.getIdProdutoBase())
                .orElseThrow(ProdutoNaoEncontradoException::new);

        IntencaoCompra intencaoCompra = new IntencaoCompra();
        intencaoCompra.setUsuario(usuario);
        intencaoCompra.setProdutoBase(produtoBase);
        intencaoCompra.setDescricaoLocal(input.getDescricaoLocal());
        intencaoCompra.setObservacoes(input.getObservacoes());
        intencaoCompra.setUrlFotoLocal(input.getUrlFotoLocal());
        intencaoCompra.setTsCriacaoIntencao(LocalDateTime.now());
        intencaoCompra.setEstaAtivo(true);

        IntencaoCompra intencaoSalva = intencaoCompraRepository.save(intencaoCompra);
        return IntencaoCompraOutputDto.fromEntity(intencaoSalva);
    }

    private Usuario buscarUsuarioLogado() {
        String email = SecurityUtils.getEmailUsuarioLogado();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }
}

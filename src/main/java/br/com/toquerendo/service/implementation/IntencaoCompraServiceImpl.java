package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.CriarIntencaoCompraInputDto;
import br.com.toquerendo.dto.output.BanhistaComIntencoesOutputDto;
import br.com.toquerendo.dto.output.IntencaoCompraOutputDto;
import br.com.toquerendo.entity.IntencaoCompra;
import br.com.toquerendo.entity.Praia;
import br.com.toquerendo.entity.ProdutoBase;
import br.com.toquerendo.entity.Usuario;
import br.com.toquerendo.entity.Vendedor;
import br.com.toquerendo.enums.CategoriaUsuarioEnum;
import br.com.toquerendo.exception.ProdutoNaoEncontradoException;
import br.com.toquerendo.repository.IntencaoCompraRepository;
import br.com.toquerendo.repository.ProdutoBaseRepository;
import br.com.toquerendo.repository.ProdutoEspecificoRepository;
import br.com.toquerendo.repository.UsuarioRepository;
import br.com.toquerendo.repository.VendedorRepository;
import br.com.toquerendo.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class IntencaoCompraServiceImpl {

    private final IntencaoCompraRepository intencaoCompraRepository;

    private final ProdutoBaseRepository produtoBaseRepository;

    private final UsuarioRepository usuarioRepository;

    private final ProdutoEspecificoRepository produtoEspecificoRepository;

    private final VendedorRepository vendedorRepository;

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

    @Transactional(readOnly = true)
    public List<BanhistaComIntencoesOutputDto> consultarBanhistasComIntencaoDeCompra() {
        Vendedor vendedor = buscarVendedorLogado();

        List<Long> idsProdutoBase = produtoEspecificoRepository
                .findAllByVendedorIdAndProdutoAtivoTrue(vendedor.getId())
                .stream()
                .map(produtoEspecifico -> produtoEspecifico.getProdutoBase().getId())
                .distinct()
                .toList();

        if (idsProdutoBase.isEmpty()) {
            return List.of();
        }

        Praia praia = vendedor.getUsuario().getPraia();
        if (praia == null) {
            return List.of();
        }

        List<IntencaoCompra> intencoes = intencaoCompraRepository.findAllAtivasDeTuristasPorPraiaEProdutosBase(
                CategoriaUsuarioEnum.TURISTA.getId(), praia.getId(), idsProdutoBase);

        Map<Long, List<IntencaoCompra>> intencoesPorBanhista = intencoes.stream()
                .collect(Collectors.groupingBy(
                        intencao -> intencao.getUsuario().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        return intencoesPorBanhista.values().stream()
                .map(grupo -> BanhistaComIntencoesOutputDto.fromEntities(grupo.get(0).getUsuario(), grupo))
                .toList();
    }

    private Usuario buscarUsuarioLogado() {
        String email = SecurityUtils.getEmailUsuarioLogado();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    private Vendedor buscarVendedorLogado() {
        String email = SecurityUtils.getEmailUsuarioLogado();
        return vendedorRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new RuntimeException("Vendedor não encontrado."));
    }
}

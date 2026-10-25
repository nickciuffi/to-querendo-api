package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.CriarVendedorRequestDto;
import br.com.toquerendo.dto.output.UsuarioOutputDto;
import br.com.toquerendo.dto.output.VendedorLocalizacaoProdutosOutputDto;
import br.com.toquerendo.entity.*;
import br.com.toquerendo.enums.CategoriaUsuarioEnum;
import br.com.toquerendo.exception.*;
import br.com.toquerendo.repository.*;
import br.com.toquerendo.security.JwtService;
import br.com.toquerendo.service.VendedorService;
import br.com.toquerendo.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VendedorServiceImpl implements VendedorService {

    private static final Pattern NUMERO_INTEIRO = Pattern.compile("\\d{1,18}");

    private VendedorRepository vendedorRepository;

    private UsuarioRepository usuarioRepository;

    private ProdutoEspecificoRepository produtoEspecificoRepository;

    private ProdutoBaseRepository produtoBaseRepository;

    private PraiaRepository praiaRepository;

    private LocalizacaoRepository localizacaoRepository;

    private JwtService jwtService;

    public UsuarioOutputDto cadastrarVendedor(CriarVendedorRequestDto criarVendedorRequest) {
        String email = SecurityUtils.getEmailUsuarioLogado();

        if (vendedorRepository.existsByUsuarioEmail(email)) {
            throw new VendedorJaCadastradoException();
        }

        Usuario usuario = usuarioRepository.findByEmailAndContaAtivaTrue(email)
                .orElseThrow(() -> new RuntimeApiException("Usuário não encontrado."));

        this.verificarSeUsuarioPodeSerVendedor(usuario);

        Vendedor vendedor = new Vendedor();
        vendedor.setUsuario(usuario);
        vendedor.setDescricao(criarVendedorRequest.getDescricao());
        vendedor.setOnline(false);

        Vendedor vendedorSalvo = vendedorRepository.save(vendedor);

        if(!usuario.getCategoria().getId().equals(CategoriaUsuarioEnum.ADMINISTRADOR.getId())) {
            usuario.setCategoriaId(CategoriaUsuarioEnum.VENDEDOR.getId());
            vendedorSalvo.setUsuario(usuarioRepository.save(usuario));
        }

        UsuarioOutputDto output = UsuarioOutputDto.fromEntity(vendedorSalvo.getUsuario());
        output.setDescricao(vendedorSalvo.getDescricao());
        output.setOnline(vendedorSalvo.getOnline());
        output.setToken(jwtService.gerarToken(output.getEmail(), output.getCategoria().getId()));

        return output;
    }

    public List<VendedorLocalizacaoProdutosOutputDto> consultarLocalizacaoVendedores(String idsProdutosBase, Long idPraia) {

        praiaRepository.findById(idPraia)
                .orElseThrow(PraiaNaoEncontradaException::new);

        Set<Long> idsProdutoBase = extrairIdsProdutosBase(idsProdutosBase);
        boolean filtrarPorProdutoBase = !idsProdutoBase.isEmpty();

        List<Vendedor> vendedores;

        if (filtrarPorProdutoBase) {
            if (produtoBaseRepository.findAllByIdInAndEstaAtivoTrue(idsProdutoBase).size() != idsProdutoBase.size()) {
                throw new ProdutoNaoEncontradoException();
            }
            vendedores = vendedorRepository.findAllVendedoresOnlinePorProdutosBaseEPraia(idsProdutoBase, idPraia);
        }
        else{
            vendedores = vendedorRepository.findAllVendedoresOnlinePorPraiaComAlgumProduto(idPraia);
        }
        return vendedores.stream()
                .map(vendedor -> {
                    Usuario usuario = vendedor.getUsuario();
                    Localizacao localizacao = localizacaoRepository.findById(usuario.getId()).orElse(null);
                    List<ProdutoEspecifico> produtosVendedor = filtrarPorProdutoBase
                            ? produtoEspecificoRepository.findAllByVendedorIdAndProdutoBaseIdInAndProdutoAtivoTrueAndProdutoBaseAtivo(vendedor.getId(), idsProdutoBase)
                            : produtoEspecificoRepository.findAllByVendedorIdAndProdutoAtivoTrueAndProdutoBaseAtivo(vendedor.getId());
                    return VendedorLocalizacaoProdutosOutputDto.fromEntities(vendedor, localizacao, produtosVendedor);
                })
                .toList();
    }

    private Set<Long> extrairIdsProdutosBase(String idsProdutosBase) {
        if (idsProdutosBase == null || idsProdutosBase.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(idsProdutosBase.split(","))
                .map(String::trim)
                .filter(token -> NUMERO_INTEIRO.matcher(token).matches())
                .map(Long::valueOf)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void verificarSeUsuarioPodeSerVendedor(Usuario usuario) {
        if(usuario.getTelefone() == null || usuario.getCpf() == null) {
            throw new CriacaoVendedorException("O usuário não possui uma conta completa. É necessário preencher o telefone e o CPF para se tornar um vendedor.");
        }
        if(!usuario.getContaAtiva()){
            throw new CriacaoVendedorException("O usuário não possui uma conta ativa. É necessário ativar a conta para se tornar um vendedor.");
        }
        if(!Objects.equals(usuario.getCategoria().getId(), CategoriaUsuarioEnum.TURISTA.getId()) && !Objects.equals(usuario.getCategoria().getId(), CategoriaUsuarioEnum.ADMINISTRADOR.getId())) {
            throw new CriacaoVendedorException("O usuário não é da categoria de turista. Apenas usuários com a categoria de turista podem se tornar vendedores.");
        }
    }
}

package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.CriarVendedorRequestDto;
import br.com.toquerendo.dto.output.VendedorLocalizacaoProdutosOutputDto;
import br.com.toquerendo.dto.output.VendedorOutputDto;
import br.com.toquerendo.entity.*;
import br.com.toquerendo.enums.CategoriaUsuarioEnum;
import br.com.toquerendo.exception.*;
import br.com.toquerendo.repository.*;
import br.com.toquerendo.service.VendedorService;
import br.com.toquerendo.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class VendedorServiceImpl implements VendedorService {

    private VendedorRepository vendedorRepository;

    private UsuarioRepository usuarioRepository;

    private ProdutoEspecificoRepository produtoEspecificoRepository;

    private ProdutoBaseRepository produtoBaseRepository;

    private PraiaRepository praiaRepository;

    private LocalizacaoRepository localizacaoRepository;

    public VendedorOutputDto cadastrarVendedor(CriarVendedorRequestDto criarVendedorRequest) {
        String email = SecurityUtils.getEmailUsuarioLogado();

        if (vendedorRepository.existsByUsuarioEmail(email)) {
            throw new VendedorJaCadastradoException();
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeApiException("Usuário não encontrado."));

        this.verificarSeUsuarioPodeSerVendedor(usuario);

        Vendedor vendedor = new Vendedor();
        vendedor.setUsuario(usuario);
        vendedor.setDescricao(criarVendedorRequest.getDescricao());
        vendedor.setOnline(true);

        Vendedor vendedorSalvo = vendedorRepository.save(vendedor);

        if(!usuario.getCategoria().getId().equals(CategoriaUsuarioEnum.ADMINISTRADOR.getId())) {
            usuario.setCategoriaId(CategoriaUsuarioEnum.VENDEDOR.getId());
            usuarioRepository.save(usuario);
        }

        return VendedorOutputDto.fromEntity(vendedorSalvo);
    }

    public VendedorOutputDto obterDadosVendedor() {
        String email = SecurityUtils.getEmailUsuarioLogado();
        Vendedor vendedorEnt = vendedorRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new RuntimeException("Vendedor não encontrado."));
        Usuario usuarioEnt = vendedorEnt.getUsuario();

        VendedorOutputDto vendedor = VendedorOutputDto.fromEntity(vendedorEnt);

        vendedor.setPraiaAtual(usuarioEnt.getPraia() != null ? usuarioEnt.getPraia().getNome() : "Sem praia vinculada");
        vendedor.setQtdProdutosAtivos(produtoEspecificoRepository.countByVendedorIdAndProdutoAtivoTrue(vendedorEnt.getId()));
        vendedor.setQtdProdutos(produtoEspecificoRepository.countByVendedorId(vendedorEnt.getId()));

        return vendedor;
    }

    public List<VendedorLocalizacaoProdutosOutputDto> consultarLocalizacaoVendedores(Long idProdutoBase, Long idPraia) {

        praiaRepository.findById(idPraia)
                .orElseThrow(PraiaNaoEncontradaException::new);

        List<Vendedor> vendedores;

        if(idProdutoBase != null){
            produtoBaseRepository.findByIdAndEstaAtivoTrue(idProdutoBase)
                    .orElseThrow(ProdutoNaoEncontradoException::new);
            vendedores = vendedorRepository.findAllVendedoresOnlinePorProdutoBaseEPraia(idProdutoBase, idPraia);
        }
        else{
            vendedores = vendedorRepository.findAllVendedoresOnlinePorPraiaComAlgumProduto(idPraia);
        }
        return vendedores.stream()
                .map(vendedor -> {
                    Usuario usuario = vendedor.getUsuario();
                    Localizacao localizacao = localizacaoRepository.findById(usuario.getId()).orElse(null);
                    List<ProdutoEspecifico> produtosVendedor = idProdutoBase != null
                            ? produtoEspecificoRepository.findAllByVendedorIdAndProdutoBaseIdAndProdutoAtivoTrueAndProdutoBaseAtivo(vendedor.getId(), idProdutoBase)
                            : produtoEspecificoRepository.findAllByVendedorIdAndProdutoAtivoTrueAndProdutoBaseAtivo(vendedor.getId());
                    return VendedorLocalizacaoProdutosOutputDto.fromEntities(vendedor, localizacao, produtosVendedor);
                })
                .toList();
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

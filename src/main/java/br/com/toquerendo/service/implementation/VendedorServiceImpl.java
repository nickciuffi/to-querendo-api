package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.CriarVendedorRequestDto;
import br.com.toquerendo.dto.output.VendedorOutputDto;
import br.com.toquerendo.entity.Usuario;
import br.com.toquerendo.entity.Vendedor;
import br.com.toquerendo.enums.CategoriaUsuarioEnum;
import br.com.toquerendo.exception.CriacaoVendedorException;
import br.com.toquerendo.exception.VendedorJaCadastradoException;
import br.com.toquerendo.repository.ProdutoEspecificoRepository;
import br.com.toquerendo.repository.UsuarioRepository;
import br.com.toquerendo.repository.VendedorRepository;
import br.com.toquerendo.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class VendedorServiceImpl {

    private VendedorRepository vendedorRepository;

    private UsuarioRepository usuarioRepository;

    private ProdutoEspecificoRepository produtoEspecificoRepository;

    public VendedorOutputDto cadastrarVendedor(CriarVendedorRequestDto criarVendedorRequest) {
        String email = SecurityUtils.getEmailUsuarioLogado();

        if (vendedorRepository.existsByUsuarioEmail(email)) {
            throw new VendedorJaCadastradoException();
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        this.verificarSeUsuarioPodeSerVendedor(usuario);

        Vendedor vendedor = new Vendedor();
        vendedor.setUsuario(usuario);
        vendedor.setDescricao(criarVendedorRequest.getDescricao());
        vendedor.setOnline(true);

        Vendedor vendedorSalvo = vendedorRepository.save(vendedor);

        usuario.setCategoriaId(CategoriaUsuarioEnum.VENDEDOR.getId());
        usuarioRepository.save(usuario);

        return VendedorOutputDto.fromEntity(vendedorSalvo);
    }

    public VendedorOutputDto consultarVendedorLogado() {
        String email = SecurityUtils.getEmailUsuarioLogado();
        Vendedor vendedorEnt = vendedorRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new RuntimeException("Vendedor não encontrado."));
        Usuario usuarioEnt = vendedorEnt.getUsuario();

        VendedorOutputDto vendedor = VendedorOutputDto.fromEntity(vendedorEnt);

        vendedor.setPraiaAtual(usuarioEnt.getPraia() != null ? usuarioEnt.getPraia().getNome() : "Sem praia vinculada");
        vendedor.setQtdProdutos(produtoEspecificoRepository.countByIdVendedor(vendedorEnt.getId()));

        return vendedor;
    }

    private void verificarSeUsuarioPodeSerVendedor(Usuario usuario) {
        if(usuario.getTelefone() == null || usuario.getCpf() == null) {
            throw new CriacaoVendedorException("O usuário não possui uma conta completa. É necessário preencher o telefone e o CPF para se tornar um vendedor.");
        }
        if(!usuario.getContaAtiva()){
            throw new CriacaoVendedorException("O usuário não possui uma conta ativa. É necessário ativar a conta para se tornar um vendedor.");
        }
        if(!Objects.equals(usuario.getCategoria().getId(), CategoriaUsuarioEnum.TURISTA.getId())){
            throw new CriacaoVendedorException("O usuário não é da categoria de turista. Apenas usuários com a categoria de turista podem se tornar vendedores.");
        }
    }
}

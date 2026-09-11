package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.AtualizarLocalizacaoInputDto;
import br.com.toquerendo.dto.output.LocalizacaoOutputDto;
import br.com.toquerendo.dto.output.VendedorLocalizacaoOutputDto;
import br.com.toquerendo.entity.Localizacao;
import br.com.toquerendo.entity.Usuario;
import br.com.toquerendo.exception.PraiaNaoEncontradaException;
import br.com.toquerendo.exception.ProdutoNaoEncontradoException;
import br.com.toquerendo.repository.LocalizacaoRepository;
import br.com.toquerendo.repository.PraiaRepository;
import br.com.toquerendo.repository.ProdutoBaseRepository;
import br.com.toquerendo.repository.ProdutoEspecificoRepository;
import br.com.toquerendo.repository.UsuarioRepository;
import br.com.toquerendo.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class LocalizacaoServiceImpl {

    private final LocalizacaoRepository localizacaoRepository;

    private final UsuarioRepository usuarioRepository;

    private final ProdutoBaseRepository produtoBaseRepository;

    private final ProdutoEspecificoRepository produtoEspecificoRepository;

    private final PraiaRepository praiaRepository;

    @Transactional
    public LocalizacaoOutputDto atualizarLocalizacao(AtualizarLocalizacaoInputDto input) {
        Usuario usuario = buscarUsuarioLogado();

        Localizacao localizacao = localizacaoRepository.findById(usuario.getId())
                .orElseGet(() -> {
                    Localizacao nova = new Localizacao();
                    nova.setUsuario(usuario);
                    return nova;
                });

        localizacao.setLatitude(input.getLatitude());
        localizacao.setLongitude(input.getLongitude());

        Localizacao localizacaoSalva = localizacaoRepository.save(localizacao);
        return LocalizacaoOutputDto.fromEntity(localizacaoSalva);
    }

    private Usuario buscarUsuarioLogado() {
        String email = SecurityUtils.getEmailUsuarioLogado();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }
}

package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.AtualizarPraiaInputDto;
import br.com.toquerendo.dto.input.CriarPraiaInputDto;
import br.com.toquerendo.dto.output.PraiaOutputDto;
import br.com.toquerendo.entity.Praia;
import br.com.toquerendo.exception.PraiaNaoEncontradaException;
import br.com.toquerendo.repository.PraiaRepository;
import br.com.toquerendo.repository.VendedorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PraiaServiceImpl {

    private final PraiaRepository praiaRepository;

    private final VendedorRepository vendedorRepository;

    public PraiaOutputDto criarPraia(CriarPraiaInputDto input) {
        Praia praia = praiaRepository.save(CriarPraiaInputDto.toEntity(input));
        return PraiaOutputDto.fromEntity(praia);
    }

    public List<PraiaOutputDto> consultarPraias() {
        return praiaRepository.findAll()
                .stream()
                .map(PraiaOutputDto::fromEntity)
                .toList();
    }

    public PraiaOutputDto consultarPraiaPorId(Long id) {
        Praia praia = praiaRepository.findById(id)
                .orElseThrow(PraiaNaoEncontradaException::new);

        PraiaOutputDto outputDto = PraiaOutputDto.fromEntity(praia);
        outputDto.setQtdVendedoresOnline(vendedorRepository.countVendedoresPorPraia(id));
        return outputDto;
    }

    public PraiaOutputDto atualizarPraia(Long id, AtualizarPraiaInputDto input) {
        Praia praia = praiaRepository.findById(id)
                .orElseThrow(PraiaNaoEncontradaException::new);

        praia.setNome(input.getNome() != null ? input.getNome() : praia.getNome());
        praia.setUrlFoto(input.getUrlFoto() != null ? input.getUrlFoto() : praia.getUrlFoto());
        praia.setCidade(input.getCidade() != null ? input.getCidade() : praia.getCidade());
        praia.setEstado(input.getEstado() != null ? input.getEstado() : praia.getEstado());

        Praia atualizada = praiaRepository.save(praia);
        return PraiaOutputDto.fromEntity(atualizada);
    }

    public void deletarPraia(Long id) {
        Praia praia = praiaRepository.findById(id)
                .orElseThrow(PraiaNaoEncontradaException::new);

        praiaRepository.delete(praia);
    }
}

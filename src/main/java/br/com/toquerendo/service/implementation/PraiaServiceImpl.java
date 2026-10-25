package br.com.toquerendo.service.implementation;

import br.com.toquerendo.dto.input.AtualizarPraiaInputDto;
import br.com.toquerendo.dto.input.CriarPraiaInputDto;
import br.com.toquerendo.dto.output.PraiaOutputDto;
import br.com.toquerendo.entity.Localizacao;
import br.com.toquerendo.entity.Praia;
import br.com.toquerendo.exception.PraiaNaoEncontradaException;
import br.com.toquerendo.repository.LocalizacaoRepository;
import br.com.toquerendo.repository.PraiaRepository;
import br.com.toquerendo.repository.VendedorRepository;
import br.com.toquerendo.service.PraiaService;
import br.com.toquerendo.utils.GeoUtils;
import br.com.toquerendo.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PraiaServiceImpl implements PraiaService {

    private static final double RAIO_PRAIAS_PROXIMAS_KM = 30.0;

    private final PraiaRepository praiaRepository;

    private final VendedorRepository vendedorRepository;

    private final LocalizacaoRepository localizacaoRepository;

    private record PraiaComDistancia(Praia praia, double distanciaKm) {
    }

    public PraiaOutputDto criarPraia(CriarPraiaInputDto input) {
        Praia praia = praiaRepository.save(CriarPraiaInputDto.toEntity(input));
        return PraiaOutputDto.fromEntity(praia);
    }

    public List<PraiaOutputDto> consultarTodasPraias() {
        return praiaRepository.findAll()
                .stream()
                .map(PraiaOutputDto::fromEntity)
                .toList();
    }

    public List<PraiaOutputDto> consultarPraiasProximas() {
        Optional<Localizacao> localizacaoUsuario = localizacaoRepository.findByUsuarioEmail(SecurityUtils.getEmailUsuarioLogado());
        if (localizacaoUsuario.isEmpty()) {
            return List.of();
        }

        double latitudeUsuario = localizacaoUsuario.get().getLatitude().doubleValue();
        double longitudeUsuario = localizacaoUsuario.get().getLongitude().doubleValue();

        return praiaRepository.findAllByLatitudeIsNotNullAndLongitudeIsNotNull()
                .stream()
                .map(praia -> new PraiaComDistancia(praia, GeoUtils.distanciaEmKm(
                        latitudeUsuario, longitudeUsuario,
                        praia.getLatitude().doubleValue(), praia.getLongitude().doubleValue())))
                .filter(praiaComDistancia -> praiaComDistancia.distanciaKm() <= RAIO_PRAIAS_PROXIMAS_KM)
                .sorted(Comparator.comparingDouble(PraiaComDistancia::distanciaKm))
                .map(praiaComDistancia -> {
                    PraiaOutputDto output = PraiaOutputDto.fromEntity(praiaComDistancia.praia());
                    output.setDistanciaKm(Math.round(praiaComDistancia.distanciaKm() * 10) / 10.0);
                    return output;
                })
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
        praia.setLatitude(input.getLatitude() != null ? input.getLatitude() : praia.getLatitude());
        praia.setLongitude(input.getLongitude() != null ? input.getLongitude() : praia.getLongitude());

        Praia atualizada = praiaRepository.save(praia);
        return PraiaOutputDto.fromEntity(atualizada);
    }

    public void deletarPraia(Long id) {
        Praia praia = praiaRepository.findById(id)
                .orElseThrow(PraiaNaoEncontradaException::new);

        praiaRepository.delete(praia);
    }
}

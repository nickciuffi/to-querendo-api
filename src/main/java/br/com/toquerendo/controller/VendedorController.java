package br.com.toquerendo.controller;

import br.com.toquerendo.dto.ApiResponse;
import br.com.toquerendo.dto.input.CriarVendedorRequestDto;
import br.com.toquerendo.dto.output.VendedorOutputDto;
import br.com.toquerendo.service.implementation.VendedorServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vendedor")
@AllArgsConstructor
public class VendedorController {

    private VendedorServiceImpl vendedorService;

    @PostMapping("")
    ResponseEntity<ApiResponse<VendedorOutputDto>> cadastrarVendedor(@Valid @RequestBody CriarVendedorRequestDto criarVendedorRequest) {
        VendedorOutputDto output = vendedorService.cadastrarVendedor(criarVendedorRequest);
        return ResponseEntity.ok().body(new ApiResponse<>(output, "Vendedor cadastrado com sucesso!"));
    }
}

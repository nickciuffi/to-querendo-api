package br.com.toquerendo.service;

import br.com.toquerendo.dto.input.AtualizarUsuarioRequestDto;
import br.com.toquerendo.dto.input.CadastroUsuarioRequestDto;
import br.com.toquerendo.dto.output.UsuarioOutputDto;

public interface UsuarioService {

    UsuarioOutputDto cadastrarUsuario(CadastroUsuarioRequestDto cadastroRequest);

    UsuarioOutputDto editarUsuario(AtualizarUsuarioRequestDto req);

    UsuarioOutputDto consultarUsuarioAutenticado();
}

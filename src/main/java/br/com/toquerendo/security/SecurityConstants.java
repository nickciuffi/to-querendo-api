package br.com.toquerendo.security;

public final class SecurityConstants {

    public static final String[] ROTAS_PUBLICAS = {
            "/auth/login",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/usuario/cadastrar",
    };

    private SecurityConstants() {
    }
}

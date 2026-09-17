package br.com.toquerendo.security;

import br.com.toquerendo.dto.ApiUser;
import br.com.toquerendo.enums.CategoriaUsuarioEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracaoMs;

    public JwtService(@Value("${security.jwt.secret}") String secret,
                       @Value("${security.jwt.expiration-ms}") long expiracaoMs) {
        this.chave = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiracaoMs = expiracaoMs;
    }

    public String gerarToken(String email, Integer categoria) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expiracaoMs);
        return Jwts.builder()
                .subject(email)
                .claim("roles", CategoriaUsuarioEnum.fromId(categoria).getRoles())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(chave)
                .compact();
    }

    public long getExpiracaoMs() {
        return expiracaoMs;
    }

    public Optional<ApiUser> validarTokenEExtrairInformacoes(String token) throws JwtException {

        Claims claims = Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);

        if (roles == null || roles.isEmpty()) {
            return Optional.empty();
        }

        ApiUser apiUser = new ApiUser();
        apiUser.setEmail(claims.getSubject());
        apiUser.setRoles(roles);
        return Optional.of(apiUser);

    }
}

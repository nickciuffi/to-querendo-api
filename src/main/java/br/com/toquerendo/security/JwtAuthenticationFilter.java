package br.com.toquerendo.security;

import br.com.toquerendo.dto.ApiUser;
import br.com.toquerendo.enums.CategoriaUsuarioEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        return Arrays.stream(SecurityConstants.ROTAS_PUBLICAS)
                .anyMatch(rotaPublica -> PATH_MATCHER.match(rotaPublica, path));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            ApiUser user;
            try {
                Optional<ApiUser> userOpt = jwtService.validarTokenEExtrairEmail(token);
                if(userOpt.isPresent()) {
                    user = userOpt.get();
                }
                else{
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.addHeader("Content-Type", "application/json");
                    response.getWriter().write("{\n" +
                            "    \"messages\": [\"Token inválido ou expirado\"]\n" +
                            "}");
                    return;
                }
            }
            catch(Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.addHeader("Content-Type", "application/json");
                response.getWriter().write("{\n" +
                        "    \"messages\": [\"Token inválido ou expirado\"]\n" +
                        "}");
                return;
            }

            if (user.getCategoria() != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<SimpleGrantedAuthority> authorities = CategoriaUsuarioEnum.fromId(user.getCategoria())
                        .getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                        .toList();

                var authentication = new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null,
                        authorities
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}

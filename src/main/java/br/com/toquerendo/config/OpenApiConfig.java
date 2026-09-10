package br.com.toquerendo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "toQuerendo API",
                version = "0.0.1",
                description = "API REST da plataforma toQuerendo, responsável por conectar turistas a vendedores ambulantes em praias, "
                        + "incluindo autenticação, cadastro de usuários e vendedores, e gestão de produtos.",
                contact = @Contact(name = "Equipe toQuerendo"),
                license = @License(name = "Uso restrito")
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "Token JWT retornado pelo endpoint de login. Deve ser enviado no header Authorization no formato: Bearer {token}"
)
public class OpenApiConfig {
}

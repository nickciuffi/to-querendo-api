package br.com.toquerendo.utils;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String getEmailUsuarioLogado(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static boolean usuarioPossuiRole(String role){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role));
    }
}

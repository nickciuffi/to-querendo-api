package br.com.toquerendo.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String getEmailUsuarioLogado(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}

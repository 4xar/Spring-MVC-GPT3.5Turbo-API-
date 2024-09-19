package com.university.project;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

public class AuthSuccess extends SavedRequestAwareAuthenticationSuccessHandler {

    //    For multiple roles in the future

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        boolean USER = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("USER"));
        if (USER) {
            setDefaultTargetUrl("/dashboard");
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}

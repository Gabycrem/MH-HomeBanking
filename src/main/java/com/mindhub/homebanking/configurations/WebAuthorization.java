package com.mindhub.homebanking.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@EnableWebSecurity
@Configuration
public class WebAuthorization{

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.POST, "/api/login", "/api/clients").permitAll()
                .requestMatchers("/web/**", "/").permitAll()
                .requestMatchers("/h2-console/**").hasAuthority("ADMIN")
                .requestMatchers("/api/clients/current", "/api/logout", "/api/clients/current/accounts",
                        "/api/clients/current/cards", "/api/transactions/**", "/api/accounts/{id}")
                    .hasAnyAuthority("CLIENT", "ADMIN")
                .requestMatchers("/admin/**", "/rest/**", "/api/clients", "/api/clients/*", "/api/accounts", "/api/cards", "/api/transaction/**")
                    .hasAuthority("ADMIN")
                .anyRequest().authenticated())
                .formLogin(form -> form
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .loginPage("/api/login")
                        .successHandler((req, res, auth) -> clearAuthenticationAttributes(req))
                        .failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()))
                // The supplied academic frontend uses session cookies without a CSRF token.
                // Keep this explicit for the demo and do not reuse it as production banking config.
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**").disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        http.exceptionHandling(exception -> exception.authenticationEntryPoint((req, res, exc) ->
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED)));

        return http.build();
    }

    private void clearAuthenticationAttributes(HttpServletRequest req) {
        HttpSession session = req.getSession(false);

        if (session != null){
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}

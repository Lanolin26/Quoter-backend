package ru.lanolin.quoter.backend.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.lanolin.quoter.backend.security.JwtCsrfFilter;
import ru.lanolin.quoter.backend.security.JwtTokenRepository;
import ru.lanolin.quoter.backend.security.UserService;

@Configuration()
@EnableWebSecurity
public class WebSecurityConfig {

    private final HandlerExceptionResolver resolver;
    private final UserService userService;
    private final JwtTokenRepository tokenRepository;

    public WebSecurityConfig(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
                             UserService userService,
                             JwtTokenRepository tokenRepository) {
        this.resolver = resolver;
        this.userService = userService;
        this.tokenRepository = tokenRepository;
    }

    private JwtCsrfFilter jwtCsrfFilter() {
        return new JwtCsrfFilter(tokenRepository, userService, resolver);
    }

    //
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           @Qualifier("securityExpressionHandler") SecurityExpressionHandler<FilterInvocation> securityExpressionHandler) throws Exception {
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .addFilterAt(jwtCsrfFilter(), CsrfFilter.class)
                .csrf().ignoringAntMatchers("/**")
                .and()
                .authorizeRequests()
                .expressionHandler(securityExpressionHandler)
                .antMatchers("/js/**", "/css/**", "/fonts/**", "/favicon.ico", "/auth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .logout().permitAll()
                .and()
                .httpBasic().authenticationEntryPoint(((request, response, e) -> resolver.resolveException(request, response, null, e)))
        ;
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       UserDetailsService userService,
                                                       @Qualifier("instancePasswordEncoder") PasswordEncoder passwordEncoder) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

}

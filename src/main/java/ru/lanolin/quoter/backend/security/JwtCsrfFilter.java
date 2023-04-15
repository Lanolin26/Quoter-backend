package ru.lanolin.quoter.backend.security;

import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@CommonsLog
public class JwtCsrfFilter extends OncePerRequestFilter {

    private final JwtTokenRepository tokenRepository;
    private final UserService userService;
    private final HandlerExceptionResolver resolver;

    public JwtCsrfFilter(JwtTokenRepository tokenRepository,
                         UserService userService,
                         @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.tokenRepository = tokenRepository;
        this.userService = userService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        request.setAttribute(HttpServletResponse.class.getName(), response);
        CsrfToken csrfToken = tokenRepository.loadToken(request);

        if (csrfToken == null) {
            csrfToken = tokenRepository.generateToken(request);
        }

        if (request.getServletPath().equals("/auth/login")) {
            try {
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                resolver.resolveException(request, response, null, new MissingCsrfTokenException(""));
            }
            return;
        }

        request.setAttribute(CsrfToken.class.getName(), csrfToken);
//        request.setAttribute(csrfToken.getParameterName(), csrfToken);

        String csrfTokenHeaderName = csrfToken.getHeaderName();
        String actualToken = request.getHeader(csrfTokenHeaderName);
        if (actualToken == null) {
            actualToken = Optional.ofNullable(request.getCookies())
                    .stream()
                    .flatMap(Arrays::stream)
                    .filter(cookie -> csrfTokenHeaderName.equalsIgnoreCase(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(csrfToken.getToken());
        }

        SecurityContext context = SecurityContextHolder.getContext();
        if (Objects.isNull(context.getAuthentication())) {
            if (StringUtils.hasText(actualToken) && tokenRepository.validateToken(actualToken)) {
                String username = tokenRepository.extractClaim(actualToken, Claims::getIssuer);
                UserDetails userDetails = userService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, actualToken, userDetails.getAuthorities());
                context.setAuthentication(usernamePasswordAuthenticationToken);
                filterChain.doFilter(request, response);
            } else {
                resolver.resolveException(request, response, null, new InvalidCsrfTokenException(csrfToken, actualToken));
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}

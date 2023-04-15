package ru.lanolin.quoter.backend.security;

import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Repository;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.domain.UserRoles;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@CommonsLog
@Repository
public class JwtTokenRepository implements CsrfTokenRepository {

    private static final String X_CSRF_TOKEN_NAME = "x-csrf-token";

    @Value("${token.expired:30}")
    private int tokenExpired;

    @Getter
    @Value("${token.secret:23qweasd345dsfsd}")
    private String secret = "";

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        String id = UUID.randomUUID().toString().replace("-", "");
        Date now = new Date();
        Date exp = Date.from(LocalDateTime.now().plusMinutes(tokenExpired).atZone(ZoneId.systemDefault()).toInstant());

        String token = "";
        try {
            token = Jwts.builder()
                    .setId(id)
                    .claim("roles", List.of(UserRoles.ANON))
                    .setIssuer("ANON")
                    .setIssuedAt(now)
                    .setNotBefore(now)
                    .setExpiration(exp)
                    .signWith(SignatureAlgorithm.HS256, secret)
                    .compact();
        } catch (JwtException e) {
            e.printStackTrace();
        }
        return new DefaultCsrfToken(X_CSRF_TOKEN_NAME, "_csrf", token);
    }

    public CsrfToken generateToken(UserEntity userEntity) {
        String id = UUID.randomUUID().toString().replace("-", "");
        Date now = new Date();
        Date exp = Date.from(LocalDateTime.now().plusMinutes(tokenExpired).atZone(ZoneId.systemDefault()).toInstant());

        String token = "";
        try {
            token = Jwts.builder()
                    .setId(id)
                    .setIssuer(userEntity.getLogin())
                    .claim("roles", userEntity.getRoles())
                    .setIssuedAt(now)
                    .setNotBefore(now)
                    .setExpiration(exp)
                    .signWith(SignatureAlgorithm.HS256, secret)
                    .compact();
        } catch (JwtException e) {
            e.printStackTrace();
        }
        return new DefaultCsrfToken(X_CSRF_TOKEN_NAME, "_csrf", token);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        if (Objects.nonNull(token)) {
            if (!response.getHeaderNames().contains(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS))
                response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, token.getHeaderName());

            if (response.getHeaderNames().contains(token.getHeaderName()))
                response.setHeader(token.getHeaderName(), token.getToken());
            else
                response.addHeader(token.getHeaderName(), token.getToken());

            Cookie cookie = new Cookie(token.getHeaderName(), token.getToken());
            cookie.setMaxAge(this.tokenExpired * 60);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public <T> T extractClaim(Claims claims, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(claims);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean validateToken(String csrfToken) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(csrfToken);
            return true;
        } catch (SignatureException e) {
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: {}", e);
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }

    public void clearToken(HttpServletResponse response) {
        if (response.getHeaderNames().contains(X_CSRF_TOKEN_NAME))
            response.setHeader(X_CSRF_TOKEN_NAME, "");
        Cookie cookie = new Cookie(X_CSRF_TOKEN_NAME, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}

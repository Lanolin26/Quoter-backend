package ru.lanolin.quoter.backend.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@CommonsLog
public class SecurityServiceImpl implements SecurityService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenRepository tokenRepository;

    @Override
    public Optional<String> findLoggedInUsername() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getDetails())
                .filter(userDetail -> userDetail instanceof UserDetails)
                .map(userDetail -> (UserDetails) userDetail)
                .map(UserDetails::getUsername);
    }

    @Override
    public boolean autoLogin(String username, String password) {
        UserDetails userDetails = userService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            log.debug(String.format("Auto login %s successfully!", username));
            return true;
        }
        return false;
    }

    @Override
    public void autoTokenLogin(String token) {
        String username = tokenRepository.extractClaim(token, Claims::getIssuer);
        UserDetails userDetails = userService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
}

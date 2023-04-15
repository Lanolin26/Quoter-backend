package ru.lanolin.quoter.backend.security;

import java.util.Optional;

public interface SecurityService {

    Optional<String> findLoggedInUsername();

    boolean autoLogin(String username, String password);

    void autoTokenLogin(String validToken);
}

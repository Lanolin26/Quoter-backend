package ru.lanolin.quoter.backend.security;

import java.util.Optional;

public interface SecurityService {

    Optional<String> findLoggedInUsername();

    void autoLogin(String username, String password);

}

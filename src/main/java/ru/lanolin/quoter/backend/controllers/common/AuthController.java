package ru.lanolin.quoter.backend.controllers.common;

import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.security.JwtTokenRepository;
import ru.lanolin.quoter.backend.security.SecurityService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AuthController {

    private final JwtTokenRepository tokenRepository;
    private final SecurityService securityService;


    @PostMapping(path = "/login")
    @PreAuthorize("hasAuthority('ANON')")
    public void getAuthUser(@RequestBody UserEntity user,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        // TODO: base64 password

        boolean login = securityService.autoLogin(user.getLogin(), user.getPassword());
        if (login) {
            CsrfToken generatedToken = tokenRepository.generateToken(user);
            tokenRepository.saveToken(generatedToken, request, response);
            return;
//            return generatedToken.getToken();
        }

        throw new BadCredentialsException("Bad credential");
    }


    @PostMapping("/registration")
    @PreAuthorize("hasAuthority('ANON')")
    public void registration(@RequestBody UserEntity user,
                             HttpServletRequest request,
                             HttpServletResponse response,
                             BindingResult bindingResult) {
        throw new NotYetImplementedException();
//        userValidator.validate(userForm, bindingResult);
//
//        if (bindingResult.hasErrors()) {
//            return "registration";
//        }
//
//        userService.save(userForm);
//
//        securityService.autoLogin(userForm.getUsername(), userForm.getPasswordConfirm());
    }

}

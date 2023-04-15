package ru.lanolin.quoter.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.domain.UserRoles;
import ru.lanolin.quoter.backend.service.UserEntityService;

import java.util.ArrayList;
import java.util.List;

@Component()
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserService implements UserDetailsService {

    private final UserEntityService entityService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.equalsIgnoreCase("anon")) {
            return new User("anon", "anon", List.of(UserRoles.ANON));
        }

        UserEntity userEntity = entityService
                .findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(userEntity.getRoles());

        return new User(userEntity.getLogin(), userEntity.getPassword(), grantedAuthorities);
    }


}

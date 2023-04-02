package ru.lanolin.quoter.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.service.UserEntityService;

import java.util.List;
import java.util.stream.Collectors;

@Component("UserService")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserService implements UserDetailsService {

    private final UserEntityService entityService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = entityService
                .findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> grantedAuthorities = userEntity.getRoles()
                .stream()
                .map(Enum::name)
//				.map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new User(userEntity.getLogin(), userEntity.getPassword(), grantedAuthorities);
    }


}

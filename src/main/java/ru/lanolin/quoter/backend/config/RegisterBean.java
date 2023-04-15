package ru.lanolin.quoter.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

@Configuration
public class RegisterBean {

    @Bean("instancePasswordEncoder")
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
//        BCryptPasswordEncoder.BCryptVersion bCryptVersion = BCryptPasswordEncoder.BCryptVersion.$2Y;
//        return new BCryptPasswordEncoder(bCryptVersion);
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = """
                        ROLE_ADMIN > ROLE_EDITOR
                        ROLE_EDITOR > ROLE_GUEST
                        ROLE_GUEST > ROLE_ANON
                        ADMIN > EDITOR
                        EDITOR > GUEST
                        GUEST > ANON
                """;
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    @Bean
    public DefaultWebSecurityExpressionHandler securityExpressionHandler() {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }

}

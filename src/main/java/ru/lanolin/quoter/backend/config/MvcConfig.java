package ru.lanolin.quoter.backend.config;

import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

    private static final Map<String, String> resourceAndPath = new HashMap<>();

    static {
        resourceAndPath.put("/js/**", "classpath:/static/js/");
        resourceAndPath.put("/css/**", "classpath:/static/css/");
        resourceAndPath.put("/fonts/**", "classpath:/static/fonts/");
        resourceAndPath.put("/favicon.ico", "classpath:/static/favicon.ico");
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        resourceAndPath.forEach((k, v) -> registry.addResourceHandler(k).addResourceLocations(v));
    }
}

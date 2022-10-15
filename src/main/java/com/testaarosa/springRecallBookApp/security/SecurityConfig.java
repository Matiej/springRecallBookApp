package com.testaarosa.springRecallBookApp.security;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final String ROLE_ADMIN = "ROLE_ADMIN";
    private final static String[] GET_AUTH_ALL_USERS_PATTERNS = {
            "/catalog/**",
            "/uploads/**",
            "/authors/**",
    };

    private final static String[] POST_AUTH_ALL_USERS_PATTERNS = {
            "/orders",
            "/login",
    };

    private final static String[] AUTH_DOC_SWAGGER_PATTERNS = {
            "/swagger-ui/**",
            "/swaggeradmin-openapi/**",
            "/v3/api-docs",
            "/v2/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/configuration/**",
            "/swagger-resources/configuration/ui",
            "/swagger-resources/configuration/security",
            "/swagger-ui.html",
            "/swagger-ui.html/**",
            "/swagger-ui.html#/**",
            "/swagger-ui.html#/*",
            "/swagger-ui/**",
            "/swagger-ui.html#/swagger-welcome-controller/**",
            "/webjars/springfox-swagger-ui/**",
            "/webjars/**",
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();
        http
                .authorizeRequests()
                .mvcMatchers(AUTH_DOC_SWAGGER_PATTERNS).permitAll()
                .mvcMatchers(HttpMethod.GET, GET_AUTH_ALL_USERS_PATTERNS).permitAll()
                .mvcMatchers(HttpMethod.POST, POST_AUTH_ALL_USERS_PATTERNS).permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and().addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    @SneakyThrows
    private JsonUserAuthenticationFilter authenticationFilter() {
        JsonUserAuthenticationFilter filter = new JsonUserAuthenticationFilter();
        filter.setAuthenticationManager(super.authenticationManager());
        return filter;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("plainuser").password(passwordEncoder().encode("test123")).roles("USER")
                .and().withUser("kowalma@gmail.com").password(passwordEncoder().encode("test123")).roles("USER")
                .and().withUser("admin").password(passwordEncoder().encode("admin")).roles("ADMIN");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public User systemUser() {
        return new User("systemUser", "", List.of(new SimpleGrantedAuthority(ROLE_ADMIN)));
    }
}

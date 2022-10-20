package com.testaarosa.springRecallBookApp.security;

import com.testaarosa.springRecallBookApp.user.dataBase.UserEntityJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableConfigurationProperties(DefaultAdmin.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserEntityJpaRepository userEntityJpaRepository;
    private final DefaultAdmin defaultAdmin;

    private final String ROLE_ADMIN = "ROLE_ADMIN";
    
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
    private final static String[] GET_AUTH_ALL_USERS_PATTERNS = {
            "/catalog/**",
            "/uploads/**",
            "/authors/**",
    };

    private final static String[] POST_AUTH_ALL_USERS_PATTERNS = {
            "/orders",
            "/login",
            "/users"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();
        http
                .authorizeRequests()
                .mvcMatchers(HttpMethod.GET, GET_AUTH_ALL_USERS_PATTERNS).permitAll()
                .mvcMatchers(HttpMethod.POST, POST_AUTH_ALL_USERS_PATTERNS).permitAll()
                .mvcMatchers(AUTH_DOC_SWAGGER_PATTERNS).hasRole("ADMIN")
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
        auth.authenticationProvider(authProvider());

    }

    @Bean
    AuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(new BookAppUserDetailService(userEntityJpaRepository, defaultAdmin));
        return provider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public User systemUser() {
        return new User("systemUser", "", List.of(new SimpleGrantedAuthority(ROLE_ADMIN)));
    }
}

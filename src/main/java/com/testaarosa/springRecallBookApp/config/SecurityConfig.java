package com.testaarosa.springRecallBookApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final static String[] AUTH_ALL_USERS_PATTERNS = {
            "/catalog/**",
    };
    private final static String[] AUTH_ADMIN_USER = {
            "/admin/**"
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
        http
                .authorizeRequests()
                .mvcMatchers(AUTH_DOC_SWAGGER_PATTERNS).permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("plainuser")
                .password(passwordEncoder().encode("test123"))
                .authorities("USER")
                .and()
                .withUser("admin")
                .password(passwordEncoder().encode("admin"))
                .authorities("ADMIN")
                .and()
                .withUser("swaggeradmin")
                .password(passwordEncoder().encode("apiadmin"))
                .authorities("ADMIN");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

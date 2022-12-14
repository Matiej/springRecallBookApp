package com.testaarosa.springRecallBookApp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class JsonUserAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper mapper = new ObjectMapper();


    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        LoginCommand loginCommand = mapper.readValue(request.getReader(), LoginCommand.class);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                loginCommand.getUsername(), loginCommand.getPassword());
        return this.getAuthenticationManager().authenticate(token);
    }
}

package com.aipark.config.oauth;

import com.aipark.biz.service.AuthService;
import com.aipark.web.dto.MemberDto;
import com.aipark.web.dto.TokenDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRequestMapper userRequestMapper;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        MemberDto.SignRequest memberDto = userRequestMapper.toDto(oAuth2User);

        SecurityContextHolder.clearContext();

        try {
            authService.signup(memberDto);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        TokenDto.TokenResponse tokenResponse = authService.login(MemberDto.LoginRequest.builder()
                .username(memberDto.getUsername())
                .password(memberDto.getPassword())
                .build());

        writeTokenResponse(response, tokenResponse);
    }

    private void writeTokenResponse(HttpServletResponse response, TokenDto.TokenResponse tokenResponse) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(tokenResponse));
        writer.flush();
    }
}

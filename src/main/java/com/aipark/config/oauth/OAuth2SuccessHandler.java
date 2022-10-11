package com.aipark.config.oauth;

import com.aipark.biz.domain.member.Member;
import com.aipark.biz.domain.member.MemberRepository;
import com.aipark.biz.service.RedisService;
import com.aipark.config.jwt.TokenProvider;
import com.aipark.web.dto.MemberDto;
import com.aipark.web.dto.TokenDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final MemberRepository memberRepository;
    private final UserRequestMapper userRequestMapper;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        MemberDto.SignRequest memberDto = userRequestMapper.toDto(oAuth2User);

        SecurityContextHolder.clearContext();

        if (!memberRepository.existsByUsername(memberDto.getUsername())) {
            Member member = memberDto.toEntity(new BCryptPasswordEncoder());
            memberRepository.save(member);
        }

        MemberDto.LoginRequest loginRequest = MemberDto.LoginRequest.builder()
                .username(memberDto.getUsername())
                .password(memberDto.getPassword())
                .build();

        UsernamePasswordAuthenticationToken authenticationToken = loginRequest.toAuthentication();
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenDto.TokenResponse tokenResponseDto = tokenProvider.createTokenDto(authenticate);

        redisService.setValues(loginRequest.getUsername(),
                tokenResponseDto.getRefreshToken(),
                Duration.ofMillis(tokenResponseDto.getTokenExpiresIn()));

        writeTokenResponse(response, tokenResponseDto);
    }

    private void writeTokenResponse(HttpServletResponse response, TokenDto.TokenResponse tokenResponse) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(tokenResponse));
        writer.flush();
    }
}

package com.aipark.biz.service;

import com.aipark.biz.domain.member.Member;
import com.aipark.biz.domain.member.MemberRepository;
import com.aipark.config.jwt.TokenProvider;
import com.aipark.web.dto.MemberDto;
import com.aipark.web.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    @Value("${white.secret}")
    private String secret;

    @Transactional
    public MemberDto.MemberResponse signup(MemberDto.SignRequest memberRequestDto) {
        if (memberRepository.existsByUsername(memberRequestDto.getUsername())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }

        Member member = memberRequestDto.toEntity(passwordEncoder);
        return MemberDto.MemberResponse.of(memberRepository.save(member));
    }

    @Transactional
    public TokenDto.TokenResponse login(MemberDto.LoginRequest memberRequestDto) {
        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = memberRequestDto.toAuthentication();

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 PrincipalDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto.TokenResponse tokenResponseDto = tokenProvider.createTokenDto(authentication);

        // redis refresh토큰 저장
        redisService.setValues(memberRequestDto.getUsername(),
                tokenResponseDto.getRefreshToken(),
                Duration.ofMillis(1000 * 60 * 60 * 24 * 7));
        redisService.setWhiteValues(memberRequestDto.getUsername() + "_" + secret,
                tokenResponseDto.getAccessToken(),
                Duration.ofMillis(1000 * 60 * 30));
        // 5. 토큰 발급
        return tokenResponseDto;
    }

    public TokenDto.TokenResponse reIssue(TokenDto.TokenRequest tokenRequestDto) {
        if(!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())){
            throw new RuntimeException("Refresh Token 유효하지 않음");
        }

        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        String refreshToken = redisService.getValues(authentication.getName());
        if(!refreshToken.equals(tokenRequestDto.getRefreshToken())){
            throw new RuntimeException("Refresh Token 만료됨");
        }

        TokenDto.TokenResponse tokenResponseDto = tokenProvider.createTokenDto(authentication);

        redisService.setValues(authentication.getName(),
                tokenResponseDto.getRefreshToken(),
                Duration.ofMillis(tokenResponseDto.getTokenExpiresIn()));

        return tokenResponseDto;
    }

    public void logout(TokenDto.TokenRequest tokenRequestDto) {
        if(!tokenProvider.validateToken(tokenRequestDto.getAccessToken())){
            throw new RuntimeException("Access Token 유효하지 않음");
        }

        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        Long expiredAccessTokenTime = tokenProvider.getExpiration(tokenRequestDto.getAccessToken());

        redisService.setBlackValues(tokenRequestDto.getAccessToken(),
                authentication.getName(),
                Duration.ofMillis(expiredAccessTokenTime));

        redisService.deleteValues(authentication.getName());
    }
}

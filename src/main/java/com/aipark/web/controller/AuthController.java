package com.aipark.web.controller;

import com.aipark.biz.service.AuthService;
import com.aipark.web.dto.MemberDto;
import com.aipark.web.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MemberDto.MemberResponse> signup(@RequestBody MemberDto.SignRequest memberRequestDto){
        return ResponseEntity.ok(authService.signup(memberRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto.TokenResponse> login(@RequestBody MemberDto.LoginRequest memberRequestDto) {
        return ResponseEntity.ok(authService.login(memberRequestDto));
    }

    @PostMapping("/re-issue")
    public ResponseEntity<TokenDto.TokenResponse> reIssue(@RequestBody TokenDto.TokenRequest tokenRequestDto){
       return ResponseEntity.ok(authService.reIssue(tokenRequestDto));
    }

    @PostMapping("/logout")
    public String logout(@RequestBody TokenDto.TokenRequest tokenRequestDto){
        log.info("tokenRequestDto : {}", tokenRequestDto);
        authService.logout(tokenRequestDto);
        return "로그아웃되셨습니다.";
    }
}

package com.aipark.web.controller;

import com.aipark.biz.service.AuthService;
import com.aipark.web.dto.MemberDto;
import com.aipark.web.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        authService.logout(tokenRequestDto);
        return "로그아웃되셨습니다.";
    }
}

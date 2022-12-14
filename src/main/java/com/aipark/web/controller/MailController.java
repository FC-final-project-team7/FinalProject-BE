package com.aipark.web.controller;

import com.aipark.biz.service.MailService;
import com.aipark.web.dto.MailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mails")
public class MailController {
    private final MailService mailService;

    /**
     * 아이디 이메일로 보내기
     * @param sendIdRequestDto
     * @return
     */
    @PostMapping("/send-id")
    public ResponseEntity<String> sendId(@RequestBody MailDto.SendIdRequest sendIdRequestDto){
        mailService.sendId(sendIdRequestDto);
        return ResponseEntity.ok("이메일로 아이디를 보냈습니다.");
    }

    /**
     * 인증 번호 만들기
     * @param sendRequestDto
     * @return
     */
    @PostMapping("/send-key")
    public ResponseEntity<String> sendAuthKey(@RequestBody MailDto.SendAuthKeyRequest sendRequestDto){
        mailService.sendAuthKey(sendRequestDto);
        return ResponseEntity.ok("인증코드를 이메일로 보냈습니다.");
    }

    /**
     * 인증 번호 확인
     * @param requestDto
     * @return
     */
    @PostMapping("/verify")
    public ResponseEntity<MailDto.AuthKeyResponse> verifyAuthKey(@RequestBody MailDto.VerifyRequest requestDto){
        return ResponseEntity.ok(mailService.verifyEmail(requestDto));
    }
}

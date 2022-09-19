package com.aipark.web.controller;

import com.aipark.biz.service.MemberService;
import com.aipark.web.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<MemberDto.MemberResponse> getMyMemberInfo(){
        return ResponseEntity.ok(memberService.getMyInfo());
    }

}

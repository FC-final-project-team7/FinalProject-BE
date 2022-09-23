package com.aipark.web.controller;

import com.aipark.biz.service.MemberService;
import com.aipark.web.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<MemberDto.MemberResponse> getMyMemberInfo(){
        return ResponseEntity.ok(memberService.getMyInfo());
    }

    @PostMapping("/edit")
    public ResponseEntity<String> changePassword(@RequestBody MemberDto.ChangeRequest changeRequestDto){
        memberService.changePassword(changeRequestDto);
        return ResponseEntity.ok("비밀번호 성공하셨습니다.");
    }

    }

}

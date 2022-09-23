package com.aipark.web.controller;

import com.aipark.biz.service.MemberService;
import com.aipark.web.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @DeleteMapping
    public ResponseEntity<String> memberDrop(){
        memberService.memberDrop();
        return ResponseEntity.ok("회원 탈퇴 성공하셨습니다.");
    }

    @GetMapping("/check-id")
    public ResponseEntity<String> memberCheck(@RequestBody MemberDto.CheckIdRequest checkIdRequest){
        if(memberService.memberCheck(checkIdRequest)){
            return new ResponseEntity<>("중복회원이 있습니다.", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("중복회원이 없습니다.");
    }
}

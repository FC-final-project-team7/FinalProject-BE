package com.aipark.web.controller;

import com.aipark.biz.service.MemberService;
import com.aipark.web.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<MemberDto.MemberResponse> getMyMemberInfo(){
        return ResponseEntity.ok(memberService.getMyInfo());
    }

    @PutMapping
    public ResponseEntity<String> changePassword(@RequestBody MemberDto.ChangeRequest changeRequestDto){
        memberService.changePassword(changeRequestDto);
        return ResponseEntity.ok("비밀번호 변경 성공하셨습니다.");
    }

    @PostMapping("/find-id")
    public ResponseEntity<String> findId(@RequestBody MemberDto.FindIdRequest requestDto){
        log.info("아이디 조회 : {}",requestDto.toString());

        return ResponseEntity.ok("이메일로 아이디를 보냈습니당");
    }

    @PostMapping("/find-pwd")
    public ResponseEntity<String> findPwd(@RequestBody MemberDto.FindPwdRequest requestDto){
        memberService.findPwd(requestDto);
        return ResponseEntity.ok("회원이 존재합니다");
    }

    @PostMapping("/edit-pwd")
    public ResponseEntity<String> editPwd(@RequestBody MemberDto.EditPwdRequest requestDto){
        memberService.editPwd(requestDto);

        return ResponseEntity.ok("비밀번호 변경되었습니다.");
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

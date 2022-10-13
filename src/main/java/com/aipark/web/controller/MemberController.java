package com.aipark.web.controller;

import com.aipark.biz.service.MemberService;
import com.aipark.web.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    /**
     * 현재 로그인한 회원정보를 알아내는 API
     * @return 회원정보
     */
    @GetMapping
    public ResponseEntity<MemberDto.MemberResponse> getMyMemberInfo(){
        return ResponseEntity.ok(memberService.getMyInfo());
    }

    /**
     * 회원관리에서 비밀번호 변경 요청
     * @ReuqestBody changeRequestDto
     * @return 비밀번호 성공 String
     */
    @PutMapping
    public ResponseEntity<String> changePassword(@RequestBody MemberDto.ChangeRequest changeRequestDto){
        memberService.changePassword(changeRequestDto);
        return ResponseEntity.ok("비밀번호 변경 성공하셨습니다.");
    }

    /**
     * 비밀번호 찾기를 통한 비밀번호 변경
     * @param requestDto
     * @return
     */

    @PutMapping("/edit-pwd")
    public ResponseEntity<String> editPwd(@RequestBody MemberDto.EditPwdRequest requestDto){
        memberService.editPwd(requestDto);

        return ResponseEntity.ok("비밀번호 변경되었습니다.");
    }

    /**
     * 회원탈퇴
     * @return
     */
    @DeleteMapping
    public ResponseEntity<String> memberDrop(){
        memberService.memberDrop();
        return ResponseEntity.ok("회원 탈퇴 성공하셨습니다.");
    }

    /**
     * 회원중복 체크
     * @param checkIdRequest
     * @return
     */
    @GetMapping("/check-id")
    public ResponseEntity<String> memberCheck(@RequestBody MemberDto.CheckIdRequest checkIdRequest){
        if(memberService.memberCheck(checkIdRequest)){
            return new ResponseEntity<>("중복회원이 있습니다.", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("중복회원이 없습니다.");
    }
}

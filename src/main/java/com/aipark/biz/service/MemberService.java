package com.aipark.biz.service;

import com.aipark.biz.domain.member.Member;
import com.aipark.biz.domain.member.MemberRepository;
import com.aipark.config.SecurityUtil;
import com.aipark.exception.MemberErrorResult;
import com.aipark.exception.MemberException;
import com.aipark.web.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 정보 조회
     * @return
     */
    @Transactional(readOnly = true)
    public MemberDto.MemberResponse getMyInfo() {
        return memberRepository.findByUsername(SecurityUtil.getCurrentMemberName())
                .map(MemberDto.MemberResponse::of)
                .orElseThrow(() -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
    }

    /**
     * 회원 관리 페이지 - 회원 비밀번호 변경
     * @param changeRequestDto
     */
    @Transactional
    public void changePassword(MemberDto.ChangeRequest changeRequestDto) {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

        if(!passwordEncoder.matches(changeRequestDto.getCurPassword(), member.getPassword())){
            throw new MemberException(MemberErrorResult.BAD_PASSWORD);
        }
        member.changePassword(passwordEncoder.encode(changeRequestDto.getChangePassword()));
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void memberDrop(){
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

        memberRepository.delete(member);
    }

    /**
     * 회원 가입 - 회원 중복 검사
     * @param checkIdRequest
     * @return
     */
    @Transactional(readOnly = true)
    public boolean memberCheck(MemberDto.CheckIdRequest checkIdRequest) {
        return memberRepository.existsByUsername(checkIdRequest.getUsername());
    }

    /**
     * 비밀번호 찾기 - 회원 조회
     * @param requestDto
     * @return
     */
    @Transactional(readOnly = true)
    public void findPwd(MemberDto.FindPwdRequest requestDto) {
        Member member = memberRepository.findByUsername(requestDto.getUsername()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

    }

    /**
     * 비밀번호 찾기 - 비밀번호 변경
     * @param requestDto (username, password)
     */
    @Transactional
    public void editPwd(MemberDto.EditPwdRequest requestDto) {
        Member member = memberRepository.findByUsername(requestDto.getUsername()).orElseThrow(
                () ->  new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

        member.changePassword(requestDto.getPassword());
    }
}

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

    @Transactional(readOnly = true)
    public MemberDto.MemberResponse getMyInfo() {
        return memberRepository.findByUsername(SecurityUtil.getCurrentMemberName())
                .map(MemberDto.MemberResponse::of)
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다"));
    }
    @Transactional
    public void changePassword(MemberDto.ChangeRequest changeRequestDto) {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.NOT_FOUND));

        if(!passwordEncoder.matches(changeRequestDto.getCurPassword(), member.getPassword())){
            throw new MemberException(MemberErrorResult.BAD_PASSWORD);
        }
        member.changePassword(passwordEncoder.encode(changeRequestDto.getChangePassword()));
    }

    @Transactional
    public void memberDrop(){
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

        memberRepository.delete(member);
    }

}

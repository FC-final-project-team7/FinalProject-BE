package com.aipark.config.auth;

import com.aipark.biz.domain.member.Member;
import com.aipark.biz.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member memberEntity = memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username + " 을 DB에서 찾지 못함"));
        if (memberEntity == null) {
            throw new UsernameNotFoundException("사용자 정보를 찾을 수 없음");
        }
        return new PrincipalDetails(memberEntity);
    }
}

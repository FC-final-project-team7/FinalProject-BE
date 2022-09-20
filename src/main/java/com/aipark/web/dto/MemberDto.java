package com.aipark.web.dto;

import com.aipark.biz.domain.enums.Authority;
import com.aipark.biz.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MemberDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LoginRequest{
        private String username;
        private String password;

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(username, password);
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SignRequest {
        private String username;
        private String email;
        private String password;
        private String name;
        private String phoneNumber;

        public Member toEntity(PasswordEncoder passwordEncoder){
            return Member.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .name(name)
                    .phoneNumber(phoneNumber)
                    .authority(Authority.ROLE_USER)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MemberResponse{
        private String name;
        private String email;

        public static MemberResponse of(Member member){
            return MemberResponse.builder()
                    .name(member.getName())
                    .email(member.getEmail())
                    .build();
        }
    }
}

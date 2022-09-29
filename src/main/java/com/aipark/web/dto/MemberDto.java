package com.aipark.web.dto;

import com.aipark.biz.domain.enums.Authority;
import com.aipark.biz.domain.member.Member;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MemberDto {

    @NoArgsConstructor
    @Getter
    @ToString
    public static class FindIdRequest{
        private String name;
        private String email;
    }

    @NoArgsConstructor
    @Getter
    @ToString
    public static class FindPwdRequest{
        private String username;
        private String name;
        private String email;
    }

    @NoArgsConstructor
    @Getter
    @ToString
    public static class EditPwdRequest{
        private String username;
        private String password;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ChangeRequest{
        private String curPassword;
        private String changePassword;
    }

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
    public static class CheckIdRequest{
        private String username;
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

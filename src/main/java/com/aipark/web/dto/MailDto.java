package com.aipark.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class MailDto {

    @Getter
    @NoArgsConstructor
    public static class SendIdRequest {
        private String name;
        private String email;
        private String message;
    }

    @Getter
    @NoArgsConstructor
    public static class SendAuthKeyRequest {
        private String username;
        private String name;
        private String email;
        private String message;
    }

    @Getter
    @NoArgsConstructor
    public static class VerifyRequest{
        private String token;
        private String email;
        private String key;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthKeyResponse{
        private String token;

        public static AuthKeyResponse of(String token){
            return AuthKeyResponse.builder().token(token).build();
        }
    }
}

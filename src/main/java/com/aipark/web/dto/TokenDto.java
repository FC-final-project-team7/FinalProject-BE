package com.aipark.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TokenDto {

    @Getter
    @NoArgsConstructor
    public static class TokenRequest{
        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @NoArgsConstructor
    public static class TokenResponse{
        private String grantType;
        private String accessToken;
        private String refreshToken;
        private Long tokenExpiresIn;

        @Builder
        public TokenResponse(String grantType, String accessToken, String refreshToken, Long tokenExpiresIn) {
            this.grantType = grantType;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.tokenExpiresIn = tokenExpiresIn;
        }
    }
}

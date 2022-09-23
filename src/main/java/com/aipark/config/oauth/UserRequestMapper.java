package com.aipark.config.oauth;

import com.aipark.web.dto.MemberDto;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserRequestMapper {
    public MemberDto.SignRequest toDto(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        return MemberDto.SignRequest.builder()
                .username((String) attributes.get("username"))
                .email((String) attributes.get("email"))
                .password((String) attributes.get("password"))
                .name((String) attributes.get("name"))
                .phoneNumber("010-3333-3333")
                .build();
    }
}
